package com.example.online.listener.service;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.example.online.course.dto.CourseGetResponse;
import com.example.online.course.elasticHelper.BuildCourseElasticDocument;
import com.example.online.coursemodule.service.CourseModuleService;
import com.example.online.elasticsearch.service.IndexService;
import com.example.online.event.CourseChangedEvent;
import com.example.online.event.CourseDeletedEvent;
import com.example.online.event.ModuleChangedEvent;
import com.example.online.event.ModuleDeletedEvent;
import com.example.online.helper.Indices;
import com.example.online.user.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseIndexService {
    private final BuildCourseElasticDocument buildCourseElasticDocument;
    private final IndexService indexService;
    private final CourseModuleService courseModuleService;
    private static final Logger LOG = LoggerFactory.getLogger(CourseIndexService.class);

    @Async("indexExecutor")
    @Retryable(retryFor = {
            IOException.class,
            ElasticsearchException.class,
            RuntimeException.class
    },
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5
            )
    )
    public void indexCourse(CourseChangedEvent event) {
        StopWatch sw = new StopWatch("indexCourse");
        sw.start("Start indexing course (case: Course onchange)");
        CourseGetResponse doc = buildCourseElasticDocument.getCourseDocument(event.courseId());
        indexService.upsertDocument(doc, event.courseId().toString(), Indices.COURSE_INDEX);
        sw.stop();
        LOG.info("Indexing course {} took {} ms", event.courseId(), sw.getTotalTimeMillis());
    }

    @Async("indexExecutor")
    @Retryable(retryFor = {
            IOException.class,
            ElasticsearchException.class,
            RuntimeException.class
    },
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5 // 1s, 1.5s, 3s
            )
    )
    public void indexModuleInCourse(ModuleChangedEvent event) {
        StopWatch sw = new StopWatch("indexCourse");
        sw.start("Start indexing course (case: Module onchange)");
        List<Long> courseIds = courseModuleService.getCoursesIdByModule(event.moduleId());
        for (var courseId : courseIds){
            CourseGetResponse doc = buildCourseElasticDocument.getCourseDocument(courseId);
            indexService.upsertDocument(doc, courseId.toString(), Indices.COURSE_INDEX);
        }
        sw.stop();
        LOG.info("Indexing module {} into courses took {} ms", event.moduleId(), sw.getTotalTimeMillis());
    }

    //================ DELETE INDEX ===========================
    @Async("indexExecutor")
    @Retryable(retryFor = {
            IOException.class,
            ElasticsearchException.class,
            RuntimeException.class
    },
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5
            )
    )
    public void dropCourse(CourseDeletedEvent event) {
        StopWatch sw = new StopWatch("unindexCourse");
        sw.start("Start unindexing course (case: Course onchange)");
        indexService.deleteDocument(event.courseId().toString(), Indices.COURSE_INDEX);
        sw.stop();
        LOG.info("Unindexing course {} took {} ms", event.courseId(), sw.getTotalTimeMillis());
    }

    @Async("indexExecutor")
    @Retryable(retryFor = {
            IOException.class,
            ElasticsearchException.class,
            RuntimeException.class
    },
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5 // 1s, 1.5s, 3s
            )
    )
    public void dropModuleInCourse(ModuleDeletedEvent event) {
        StopWatch sw = new StopWatch("unindexCourse");
        sw.start("Start unindexing course (case: Module onchange)");
        for (var courseId : event.courseIds()){
            CourseGetResponse doc = buildCourseElasticDocument.getCourseDocument(courseId);
            indexService.upsertDocument(doc, courseId.toString(), Indices.COURSE_INDEX);
        }
        sw.stop();
        LOG.info("Unindexing module {} into courses took {} ms", event.moduleId(), sw.getTotalTimeMillis());
    }
}
