package kr.co.junko.dto;

import java.util.Map;

import lombok.Data;

@Data
public class TemplatePreviewDTO {

    private int template_idx;
    private Map<String, String> variables;
	
}
