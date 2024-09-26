package com.sky.users.project;

import com.sky.users.project.to.CreateExternalProjectTO;
import com.sky.users.project.to.ExternalProjectTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sky.users.common.Utils.composeUserPath;

@RestController
@RequestMapping("/api/users/{userId}/projects")
public class ExternalProjectController {
    private final ExternalProjectService externalProjectService;

    public ExternalProjectController(ExternalProjectService externalProjectService) {
        this.externalProjectService = externalProjectService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Requested user not found", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid request path", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "200", description = "Requested projects provided", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @Operation(summary = "Get user's external projects", description = "Provides external projects for requested user.")
    public ResponseEntity<List<ExternalProjectTO>> getExternalProjects(@PathVariable Long userId) {
        return ResponseEntity.ok(externalProjectService.findUserExternalProjects(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Requested user not found", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "Project already exists", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "201", description = "External project created", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @Operation(summary = "Create external project", description = "Create external project for given user")
    public ResponseEntity<ExternalProjectTO> addExternalProject(@PathVariable Long userId, @RequestBody @Valid CreateExternalProjectTO createExternalProjectTO) {
        ExternalProjectTO createdProject = externalProjectService.createExternalProject(userId, createExternalProjectTO);
        return ResponseEntity.created(composeUserPath(userId, createdProject.id()))
                .body(createdProject);
    }
}
