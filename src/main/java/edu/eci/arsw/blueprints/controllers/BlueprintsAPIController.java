package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/v1/blueprints")
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) { this.services = services; }

    // GET /blueprints
    @GetMapping
    public ApiResponse<Set<Blueprint>> getAll() {
        ApiResponse<Set<Blueprint>> respuesta = new ApiResponse<Set<Blueprint>>(200,"executed ok",services.getAllBlueprints());
        return respuesta;
    }

    // GET /blueprints/{author}
    @GetMapping("/{author}")
    public  ResponseEntity<?> byAuthor(@PathVariable String author) {
        try {
            ApiResponse<Set<Blueprint>> respuesta = new ApiResponse<Set<Blueprint>>(200,"executed ok",services.getBlueprintsByAuthor(author));
            return  ResponseEntity.ok(respuesta);
        } catch (BlueprintNotFoundException e) {
            ApiResponse<Set<Blueprint>> respuesta = new ApiResponse<Set<Blueprint>>(404,"Not found",null);
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    // GET /blueprints/{author}/{bpname}
    @GetMapping("/{author}/{bpname}")
    public ResponseEntity<?> byAuthorAndName(@PathVariable String author, @PathVariable String bpname) {
        try {
            ApiResponse<Blueprint> respuesta = new ApiResponse<Blueprint>(200,"executed ok",services.getBlueprint(author, bpname));
            return ResponseEntity.ok(respuesta);
        } catch (BlueprintNotFoundException e) {
            ApiResponse<Blueprint> respuesta = new ApiResponse<Blueprint>(404,"Not found",null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    // POST /blueprints
    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody NewBlueprintRequest req) {
        try {
            Blueprint bp = new Blueprint(req.author(), req.name(), req.points());
            services.addNewBlueprint(bp);
            ApiResponse<String> respuesta = new ApiResponse<String>(201,"Created","Archivo creado correctamente");
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (BlueprintPersistenceException e) {
            ApiResponse<String> respuesta = new ApiResponse<String>(400,"Bad Request",e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(respuesta);
        }
    }

    // PUT /blueprints/{author}/{bpname}/points
    @PutMapping("/{author}/{bpname}/points")
    public ResponseEntity<?> addPoint(@PathVariable String author, @PathVariable String bpname,
                                      @RequestBody Point p) {
        try {
            services.addPoint(author, bpname, p.x(), p.y());
            ApiResponse<String> respuesta = new ApiResponse<String>(202,"Accepted","Archivo actualizado correctamente");
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(respuesta);
        } catch (BlueprintNotFoundException e) {
            ApiResponse<String> respuesta = new ApiResponse<String>(404,"Not Found",e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    public record NewBlueprintRequest(
            @NotBlank String author,
            @NotBlank String name,
            @Valid java.util.List<Point> points
    ) { }
}
