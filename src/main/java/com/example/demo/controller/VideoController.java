package com.example.demo.controller;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.ErrorCode;
import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.dto.VideoDTO;
import com.example.demo.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/videos")
@Tag(name = "视频", description = "视频上传与播放接口")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Value("${app.video.storage:uploads/videos}")
    private String storageDir;

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails cud) {
            return cud.getUserId();
        }
        return null; // 匿名也允许上传/访问？这里示例要求登录上传
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传视频", description = "multipart form: file, title, description")
    public ApiResponse<VideoDTO> upload(@RequestPart("file") MultipartFile file,
                                        @RequestPart(value = "title", required = false) String title,
                                        @RequestPart(value = "description", required = false) String description) {
        Long uid = currentUserId();
        if (uid == null) return ApiResponse.error(ErrorCode.UNAUTHORIZED, "未登录");
        if (file == null || file.isEmpty()) {
            return ApiResponse.error(ErrorCode.PARAM_ERROR, "文件不能为空");
        }
        try {
            return ApiResponse.success(videoService.upload(uid, title, description, file));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(ErrorCode.PARAM_ERROR, e.getMessage());
        } catch (IOException e) {
            return ApiResponse.error(ErrorCode.SERVER_ERROR, "文件写入失败");
        }
    }

    @GetMapping
    @Operation(summary = "分页列表", description = "page,size")
    public ApiResponse<List<VideoDTO>> page(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(videoService.page(page, size).getContent());
    }

    @GetMapping("/{id}")
    @Operation(summary = "视频元数据", description = "获取视频元信息")
    public ApiResponse<VideoDTO> get(@PathVariable Long id) {
        VideoDTO dto = videoService.get(id);
        if (dto == null) return ApiResponse.error(ErrorCode.NOT_FOUND, "不存在");
        return ApiResponse.success(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除视频", description = "删除视频文件与记录")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Long uid = currentUserId();
        if (uid == null) return ApiResponse.error(ErrorCode.UNAUTHORIZED, "未登录");
        VideoDTO dto = videoService.get(id);
        if (dto == null) return ApiResponse.error(ErrorCode.NOT_FOUND, "不存在");
        // 可扩展：校验上传者本人或管理员
        boolean ok = videoService.delete(id);
        return ok ? ApiResponse.success() : ApiResponse.error(ErrorCode.SERVER_ERROR, "删除失败");
    }

    private static final Pattern RANGE_PATTERN = Pattern.compile("bytes=(\\d*)-(\\d*)");

    @GetMapping("/{id}/stream")
    @Operation(summary = "视频流读取", description = "支持 Range 请求 (Content-Type 依据文件类型)")
    public ResponseEntity<byte[]> stream(@PathVariable Long id,
                                         @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
        var entity = videoService.getEntity(id);
        if (entity == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Path file = Paths.get(storageDir).resolve(entity.getFilename());
        if (!Files.exists(file)) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        long length = Files.size(file);
        long start = 0;
        long end = length - 1;
        boolean isPartial = false;
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            Matcher m = RANGE_PATTERN.matcher(rangeHeader);
            if (m.find()) {
                String s = m.group(1);
                String e = m.group(2);
                if (s != null && !s.isEmpty()) start = Long.parseLong(s);
                if (e != null && !e.isEmpty()) end = Long.parseLong(e);
                if (end >= length) end = length - 1;
                if (start > end) start = 0; // 容错
                isPartial = true;
            }
        }
        long contentLength = end - start + 1;
        byte[] data = new byte[(int) contentLength];
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
            raf.seek(start);
            raf.readFully(data);
        }
        HttpHeaders headers = new HttpHeaders();
        String ct = entity.getContentType();
        if (ct == null || ct.isBlank()) {
            ct = Files.probeContentType(file);
            if (ct == null) ct = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        headers.setContentType(MediaType.parseMediaType(ct));
        headers.set("Accept-Ranges", "bytes");
        if (isPartial) {
            headers.set("Content-Range", "bytes " + start + "-" + end + "/" + length);
            return new ResponseEntity<>(data, headers, HttpStatus.PARTIAL_CONTENT);
        }
        headers.setContentLength(contentLength);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
