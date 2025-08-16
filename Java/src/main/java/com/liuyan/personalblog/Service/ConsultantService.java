package com.liuyan.personalblog.Service;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel"
)
public interface ConsultantService {
    @SystemMessage("你是一个智能摘要员，主要对博客内容进行摘要，摘要内容尽量简洁，50字以内")
    String chat(@UserMessage String content);
}
