package cinema.service;

import cinema.dto.response.MovieResponse;
import java.util.List;

public interface MovieService {
    List<MovieResponse> getNowShowingMovies();
    MovieResponse getMovieById(Long id);
    List<MovieResponse> getComingSoonMovies();
}