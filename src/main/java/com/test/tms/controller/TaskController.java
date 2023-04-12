package com.test.tms.controller;

import com.test.tms.entity.SubTask;
import com.test.tms.entity.Task;
import com.test.tms.repository.TaskRepository;
import com.test.tms.service.TaskService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api")
public class TaskController {
    private TaskService taskService;
    private TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/search")
    public List<Task> searchByKeyword(@RequestParam("keyword") String keyword) {
        return taskService.searchByKeyword(keyword);
    }

    @GetMapping("/export/tasks")
    public void exportTasks(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"tasks.csv\"");
        taskService.exportTasks(response);

    }

    @PostMapping("/import/tasks")
    public ResponseEntity<String> importTasks(@RequestParam("file") MultipartFile file) throws IOException {
        List<Task> tasks = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

        // skip the header line
        reader.readLine();

        String line = "";
        String cvsSplitBy = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"; // regex pattern to allow "subTask1,subTask2" as a single column

        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(cvsSplitBy);//currently accepting only 3 header and not all

            String title = fields[0];
            String description = fields[1];
            String[] subTasksAsAString = fields[2].split(",");
            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            if (fields[2].contains(",")) {
                if (subTasksAsAString.length > 0) {
                    subTasksAsAString[0] = subTasksAsAString[0].replaceAll("^\"|\"$", "");//remove leading/trailing quotes
                    subTasksAsAString[subTasksAsAString.length - 1] = subTasksAsAString[subTasksAsAString.length - 1].replaceAll("^\"|\"$", "");// remove leading/trailing quotes
                }


                List<SubTask> subTasks = new ArrayList<>();
                for (String subTaskDesc : subTasksAsAString) {
                    SubTask subTask = new SubTask();
                    subTask.setDescription(subTaskDesc.trim());
                    subTask.setTask(task);
                    subTasks.add(subTask);
                }

                task.setSubTaskList(subTasks);
                tasks.add(task);
            }
        }
        taskService.importTasks(tasks);
        return ResponseEntity.ok("CSV imported successfully");
    }


}
