package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.NewsDTO;

import java.util.List;

public interface NewsService {

    List<NewsDTO> getAllNews(String category, String keyword);

    NewsDTO getNewsBySlug(String slug);

    NewsDTO getNewsById(Integer id);

    List<NewsDTO> getRelatedNews(Integer currentId, String category, int limit);

    List<String> getNewsCategories();

    NewsDTO getFeaturedArticle();
}
