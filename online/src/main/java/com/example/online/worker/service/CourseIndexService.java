package com.example.online.worker.service;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.example.online.course.dto.CourseGetResponse;
import com.example.online.course.elasticHelper.BuildCourseElasticDocument;
import com.example.online.coursemodule.service.CourseModuleService;
import com.example.online.elasticsearch.helper.BulkResult;
import com.example.online.elasticsearch.service.IndexService;
import com.example.online.helper.Indices;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseIndexService {
    private final BuildCourseElasticDocument buildCourseElasticDocument;
    private final IndexService indexService;
    private final CourseModuleService courseModuleService;
    private static final Logger LOG = LoggerFactory.getLogger(CourseIndexService.class);

//    @Async("indexExecutor")
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
    public BulkResult indexCourse(List<Long> courseIds) {
        StopWatch sw = new StopWatch("indexCourse");
        sw.start("Start indexing course (case: Course onchange)");
        Map<String, CourseGetResponse> results = courseIds.stream().collect(Collectors.toMap(
                Object::toString,
                buildCourseElasticDocument::getCourseDocument
        ));
        BulkResult r = indexService.bulkUpsertDocuments(results, Indices.COURSE_INDEX);
        sw.stop();
        LOG.info("Indexing course batch {} took {} ms", courseIds, sw.getTotalTimeMillis());
        return r;
    }

//    @Async("indexExecutor")
//    @Retryable(retryFor = {
//            IOException.class,
//            ElasticsearchException.class,
//            RuntimeException.class
//    },
//            maxAttempts = 3,
//            backoff = @Backoff(
//                    delay = 1000,
//                    multiplier = 1.5 // 1s, 1.5s, 3s
//            )
//    )
//    public void indexModuleInCourse(Long moduleId) {
//        StopWatch sw = new StopWatch("indexCourse");
//        sw.start("Start indexing course (case: Module onchange)");
//        List<Long> courseIds = courseModuleService.getCoursesIdByModule(moduleId);
//        for (var courseId : courseIds){
//            CourseGetResponse doc = buildCourseElasticDocument.getCourseDocument(courseId);
//            indexService.upsertDocument(doc, courseId.toString(), Indices.COURSE_INDEX);
//        }
//        sw.stop();
//        LOG.info("Indexing module {} into courses took {} ms", moduleId, sw.getTotalTimeMillis());
//    }

    //================ DELETE INDEX ===========================
//    @Async("indexExecutor")
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
    public BulkResult dropCourse(List<Long> courseIds) {
        StopWatch sw = new StopWatch("unindexCourse");
        sw.start("Start unindexing course (case: Course onchange)");
        BulkResult r = indexService.bulkDeleteDocuments(courseIds.stream().map(Object::toString).toList(), Indices.COURSE_INDEX);
        sw.stop();
        LOG.info("Unindexing course batch {} took {} ms", courseIds, sw.getTotalTimeMillis());
        return r;
    }

//    @Async("indexExecutor")
//    @Retryable(retryFor = {
//            IOException.class,
//            ElasticsearchException.class,
//            RuntimeException.class
//    },
//            maxAttempts = 3,
//            backoff = @Backoff(
//                    delay = 1000,
//                    multiplier = 1.5 // 1s, 1.5s, 3s
//            )
//    )
//    public void dropModuleInCourse(Long courseId) {
//        StopWatch sw = new StopWatch("unindexCourse");
//        sw.start("Start unindexing course (case: Module onchange)");
//        CourseGetResponse doc = buildCourseElasticDocument.getCourseDocument(courseId);
//        indexService.upsertDocument(doc, courseId.toString(), Indices.COURSE_INDEX);
//        sw.stop();
//        LOG.info("Unindexing module {} into courses took {} ms", event.moduleId(), sw.getTotalTimeMillis());
//    }
}
