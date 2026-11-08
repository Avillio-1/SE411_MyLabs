package psu.se411.lab14.repository;

import org.springframework.data.repository.ListCrudRepository;
import psu.se411.lab14.model.Course;

public interface CourseRepository extends ListCrudRepository<Course, Long> {
}
