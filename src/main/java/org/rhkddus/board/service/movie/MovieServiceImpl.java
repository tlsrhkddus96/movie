package org.rhkddus.board.service.movie;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rhkddus.board.dto.movie.MovieDTO;
import org.rhkddus.board.dto.PageRequestDTO;
import org.rhkddus.board.dto.PageResultDTO;
import org.rhkddus.board.entity.Movie;
import org.rhkddus.board.entity.MovieImage;
import org.rhkddus.board.repository.movie.MovieImageRepository;
import org.rhkddus.board.repository.movie.MovieRepository;
import org.rhkddus.board.repository.movie.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;
    private final MovieImageRepository imageRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    @Override
    public void removeWithReviews(Long movieNum) {

        reviewRepository.deleteByMovieNum(movieNum);
        imageRepository.deleteByMovieNum(movieNum);
        movieRepository.deleteById(movieNum);

    }

    @Override
    public MovieDTO getMovie(Long movieNum) {

        List<Object[]> result = movieRepository.getMovieWithAll(movieNum);

        Movie movie = (Movie) result.get(0)[0]; // Movie엔티티는 가장 앞에 존재

        //영화의 이미지 개수만큼 MovieImage객체 필요
        List<MovieImage> movieImageList = new ArrayList<>();

        result.forEach(arr -> {
            MovieImage movieImage = (MovieImage) arr[1];
            movieImageList.add(movieImage);
        });

        Double avg = (Double) result.get(0)[2]; // 평균 평점
        Long reviewCnt = (Long) result.get(0)[3]; // 리뷰 개수

        return entitiesToDTO(movie, movieImageList, avg, reviewCnt);
    }

    @Transactional
    @Override
    public Long register(MovieDTO movieDTO) {

        Map<String, Object> entityMap = dtoToEntity(movieDTO);
        Movie movie = (Movie) entityMap.get("movie");
        List<MovieImage> movieImageList =
                (List<MovieImage>) entityMap.get("imgList");

        movieRepository.save(movie);

        movieImageList.forEach(movieImage -> {

            imageRepository.save(movieImage);

        });

        return movie.getMovieNum();

    }






    @Transactional
    @Override
    public void modify(MovieDTO movieDTO) {

        Movie movie = movieRepository.getOne(movieDTO.getMovieNum());

        movie.changeTitle(movieDTO.getTitle());

        log.info("title"  + movieDTO.getTitle());
        log.info("dto" + movieDTO);

        movieRepository.save(movie);


    }

    @Override
    public PageResultDTO<MovieDTO, Object[]> getList(PageRequestDTO requestDTO) {

        Pageable pageable = requestDTO.getPageable(Sort.by("movieNum").descending());

        Page<Object[]> result = movieRepository.getListPage(pageable);


        Function<Object[], MovieDTO> fn = (arr -> entitiesToDTO(
                (Movie) arr[0],
                (List<MovieImage>) (Arrays.asList((MovieImage)arr[1])),
                (Double) arr[2],
                (Long) arr[3]
        ));

        return new PageResultDTO<>(result, fn);
    }


}
