package org.rhkddus.board.repository.movie;

import org.rhkddus.board.entity.Member;
import org.rhkddus.board.entity.Movie;
import org.rhkddus.board.entity.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"member"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Review> findByMovie(Movie movie);

    @Modifying
    @Query("delete from Review r where r.member = :member")
    void deleteByMember(Member member);


    @Modifying
    @Query("delete from Review r where r.movie.movieNum =:movieNum")
    void deleteByMovieNum(Long movieNum);


}
