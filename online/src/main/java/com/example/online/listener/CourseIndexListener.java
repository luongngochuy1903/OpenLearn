package com.example.online.listener;

import com.example.online.course.dto.CourseGetResponse;
import com.example.online.course.elasticHelper.BuildCourseElasticDocument;
import com.example.online.coursemodule.service.CourseModuleService;
import com.example.online.elasticsearch.service.IndexService;
import com.example.online.event.CourseChangedEvent;
import com.example.online.event.ModuleChangedEvent;
import com.example.online.event.PostChangedEvent;
import com.example.online.helper.Indices;
import com.example.online.post.dto.PostGetResponse;
import com.example.online.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseIndexListener {
    private final BuildCourseElasticDocument buildCourseElasticDocument;
    private final IndexService indexService;
    private final CourseModuleService courseModuleService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCourseChanged(CourseChangedEvent event) {
        CourseGetResponse doc = buildCourseElasticDocument.getCourseDocument(event.courseId());
        indexService.upsertDocument(doc, event.courseId().toString(), Indices.COURSE_INDEX);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onModuleChanged(ModuleChangedEvent event) {
        List<Long> courseIds = courseModuleService.getCoursesIdByModule(event.moduleId());
        for (var courseId : courseIds){
            CourseGetResponse doc = buildCourseElasticDocument.getCourseDocument(courseId);
            indexService.upsertDocument(doc, courseId.toString(), Indices.COURSE_INDEX);
        }
    }
}
