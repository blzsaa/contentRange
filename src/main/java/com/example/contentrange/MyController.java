package com.example.contentrange;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MyController {
  @GetMapping("lc/all")
  List<LC> getAll() {
    return List.of(new LC("a"), new LC("b"), new LC("c"));
  }
}
