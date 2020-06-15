package org.planqk.atlas.web.linkassembler;

import org.planqk.atlas.web.annotation.ApiVersion;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/controller")
@ApiVersion("v1")
public class DummyController {
    @GetMapping("/test")
    public HttpEntity<Void> test() {
        return null;
    }
}
