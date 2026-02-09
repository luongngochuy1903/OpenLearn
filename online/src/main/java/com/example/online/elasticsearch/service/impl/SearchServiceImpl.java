package com.example.online.elasticsearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.online.course.dto.CourseGetResponse;
import com.example.online.course.elasticHelper.BuildCourseElasticDocument;
import com.example.online.elasticsearch.dto.SearchRequestDTO;
import com.example.online.elasticsearch.dto.SearchResponseDTO;
import com.example.online.elasticsearch.service.SearchService;
import com.example.online.post.dto.PostGetResponse;
import com.example.online.post.elasticHelper.BuildPostElasticDocument;
import com.example.online.repository.CourseRepository;
import com.example.online.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final ElasticsearchClient client;
    private final PostRepository postRepository;
    private final CourseRepository courseRepository;
    private final BuildPostElasticDocument buildPostElasticDocument;
    private final BuildCourseElasticDocument buildCourseElasticDocument;

    public SearchResponseDTO search(SearchRequestDTO request) throws IOException {

        SearchResponse<Map> response = client.search(s -> s
                        .index(List.of("post_index", "course_index"))
                        .from(request.getPage() * request.getSize())
                        .size(request.getSize())

                        // ===== QUERY + RANKING =====
                        .query(q -> q.functionScore(fs -> fs

                                // ===== TEXT QUERY (SOFT MATCHING) =====
                                .query(inner -> inner.bool(root -> root

                                        // -------- POST --------
                                        .should(s1 -> s1.bool(b -> b
                                                .filter(f -> f.term(t -> t.field("_index").value("post_index")))

                                                .should(sm -> sm.multiMatch(mm -> mm
                                                        .query(request.getKeyword())
                                                        .fields(
                                                                "name^4",
                                                                "contentMarkdown^2",
                                                                "creator",
                                                                "communityName"
                                                        )
                                                        .fuzziness("AUTO")
                                                ))

                                                .minimumShouldMatch("1")
                                        ))

                                        // -------- COURSE --------
                                        .should(s2 -> s2.bool(b -> b
                                                .filter(f -> f.term(t -> t.field("_index").value("course_index")))

                                                .should(sm -> sm.multiMatch(mm -> mm
                                                        .query(request.getKeyword())
                                                        .fields(
                                                                "courseName^4",
                                                                "description^2",
                                                                "tagName^3",
                                                                "moduleGetResponse.name",
                                                                "moduleGetResponse.description"
                                                        )
                                                        .fuzziness("AUTO")
                                                ))

                                                .minimumShouldMatch("1")
                                        ))

                                        .minimumShouldMatch("1")
                                ))

                                // ===== POST RANKING =====
                                .functions(fn -> fn
                                        .filter(f -> f.term(t -> t.field("_index").value("post_index")))
                                        .fieldValueFactor(f -> f
                                                .field("views")
                                                .modifier(FieldValueFactorModifier.Log1p)
                                                .missing(1.0)
                                        )
                                        .weight(1.2)
                                )

                                // ===== COURSE RANKING =====
                                .functions(fn -> fn
                                        .filter(f -> f.term(t -> t.field("_index").value("course_index")))
                                        .fieldValueFactor(f -> f
                                                .field("enroll_count")
                                                .modifier(FieldValueFactorModifier.Log1p)
                                                .missing(1.0)
                                        )
                                        .weight(2.0)
                                )

                                .scoreMode(FunctionScoreMode.Sum)
                                .boostMode(FunctionBoostMode.Sum)
                        ))

                        // ===== CHỈ LẤY ID =====
                        .source(src -> src.filter(f -> f.includes("id")))
                ,
                Map.class
        );

        // ===== TÁCH ID THEO INDEX =====
        List<Long> postIds = new ArrayList<>();
        List<Long> courseIds = new ArrayList<>();

        for (Hit<Map> hit : response.hits().hits()) {
            String index = hit.index();
            Map<String, Object> source = hit.source();

            if (source == null) continue;

            Long id = Long.valueOf(source.get("id").toString());

            if ("post_index".equals(index)) {
                postIds.add(id);
            } else if ("course_index".equals(index)) {
                courseIds.add(id);
            }
        }

        // ===== QUERY DATABASE =====
        List<PostGetResponse> posts = postIds.isEmpty()
                ? List.of()
                : postRepository.findPostResponsesByIds(postIds).stream()
                .map(post -> buildPostElasticDocument.getPostDocument(post.getId())).toList();

        List<CourseGetResponse> courses = courseIds.isEmpty()
                ? List.of()
                : courseRepository.findCourseResponsesByIds(courseIds).stream()
                .map(course -> buildCourseElasticDocument.getCourseDocument(course.getId())).toList();;

        return new SearchResponseDTO(posts, courses);
    }
}
