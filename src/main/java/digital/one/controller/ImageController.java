package digital.one.controller;

import digital.one.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageController {

    private final ImageService service;

    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam("image") MultipartFile file) throws IOException {
        return ResponseEntity.status(201).body(service.uploadImage(file));
    }

    @GetMapping("/get_by_id/{id}")
    public ResponseEntity<?> download(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.downloadById(id));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestParam("image") MultipartFile file) throws IOException {
        return ResponseEntity.status(200).body(service.edit(id,file));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return ResponseEntity.status(200).body(service.delete(id));
    }

}

