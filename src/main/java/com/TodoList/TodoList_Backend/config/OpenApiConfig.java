package com.TodoList.TodoList_Backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "TodoList API", version = "1.0.0", description = "API REST pour la gestion de tâches (To-Do List)", contact = @Contact(name = "TodoList Backend", email = "contact@todolist.com")), servers = {
                @Server(url = "http://localhost:8080", description = "Développement local"),
                @Server(url = "https://todolist.com", description = "Production")
}

)
public class OpenApiConfig {
}
