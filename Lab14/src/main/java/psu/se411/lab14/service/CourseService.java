package psu.se411.lab14.service;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import psu.se411.lab14.model.Course;
import psu.se411.lab14.repository.CourseRepository;

@Service
public class CourseService {
    private final CourseRepository repository;
    private final JdbcAggregateTemplate template;

    public CourseService(CourseRepository repository, JdbcAggregateTemplate template) {
        this.repository = repository;
        this.template = template;
    }

    public List<Course> findAll() {
        return repository.findAll();
    }

    public Course findById(long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Course " + id + " was not found"));
    }

    @Transactional
    public Course create(Course course) {
        if (repository.existsById(course.id())) {
            throw duplicate(course.id());
        }
        // An assigned ID makes save() choose UPDATE. insert() explicitly creates a new row.
        try {
            return template.insert(course);
        } catch (DuplicateKeyException exception) {
            // A concurrent POST may insert this ID after the existence check.
            throw duplicate(course.id());
        }
    }

    @Transactional
    public void update(long id, Course course) {
        findById(id);
        if (course.id() != id) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The body id must match the URL id");
        }
        template.update(course);
    }

    @Transactional
    public void delete(long id) {
        findById(id);
        repository.deleteById(id);
    }

    private ResponseStatusException duplicate(long id) {
        return new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                "Course " + id + " already exists");
    }
}
