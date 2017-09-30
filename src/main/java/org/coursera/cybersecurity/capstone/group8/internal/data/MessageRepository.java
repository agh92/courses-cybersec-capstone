package org.coursera.cybersecurity.capstone.group8.internal.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

/*
 * In order to work with different repositories e.g. Databases, plain text files etc, the storage is abstracted in this
 * interface
 */
public interface MessageRepository extends CrudRepository<Message,Long> {
    /*
     * return messages sent to a specific user, ordered by the time they were sent
     */
	List<Message> findByToUserIdOrderByTimestampAsc(String userId);
}
