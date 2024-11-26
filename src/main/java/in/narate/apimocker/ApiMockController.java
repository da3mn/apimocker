package in.narate.apimocker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class ApiMockController {

    @Autowired
    private ApiMockService apiMockService;

    @GetMapping
    public String showMockConfigPage(Model model) {
        model.addAttribute("mockApis", apiMockService.getAllMocks());
        return "mock-config"; // Return the Thymeleaf template name without .html
    }

    @PostMapping("/mock-config/add")
    public String addMockConfig(ApiMockConfig config) {
        apiMockService.addApiMockConfig(config);
        return "redirect:/";
    }

    @DeleteMapping("/mock-config/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteMockConfig(@PathVariable Long id) {
        apiMockService.deleteMockConfig(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mock-config/update")
    public String updateMockConfig(ApiMockConfig updatedConfig) {
        ApiMockConfig existingConfig = apiMockService.findMockConfigById(updatedConfig.getId());
        if (existingConfig != null) {
            existingConfig.setEndpoint(updatedConfig.getEndpoint());
            existingConfig.setRequestBody(updatedConfig.getRequestBody());
            existingConfig.setResponseBody(updatedConfig.getResponseBody());
            existingConfig.setRequestType(updatedConfig.getRequestType());
            existingConfig.setResponseType(updatedConfig.getResponseType());
            existingConfig.setOriginalServerUrl(updatedConfig.getOriginalServerUrl());
            apiMockService.updateApiMockConfig(existingConfig);
        }
        return "redirect:/";
    }

}
