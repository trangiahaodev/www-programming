package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.NewsDTO;
import iuh.wwwprogramming.dto.OfficeDTO;
import iuh.wwwprogramming.dto.VoucherDTO;

import java.util.List;

public interface HomeContentService {

    List<OfficeDTO> getOffices();

    List<NewsDTO> getFeaturedNews(int limit);

    List<VoucherDTO> getActiveVouchers();

    long getTotalActiveProducts();

    long getTotalCategories();
}
