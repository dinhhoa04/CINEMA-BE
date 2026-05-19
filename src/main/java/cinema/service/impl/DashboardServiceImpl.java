package cinema.service.impl;

import cinema.service.DashboardService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> result = new HashMap<>();

        // ==========================================
        // 1. THỐNG KÊ NHANH (STAT CARDS)
        // ==========================================
        Map<String, Object> stats = new HashMap<>();

        // Tổng doanh thu (Chỉ tính vé PAID và CHECKED_IN)
        BigDecimal totalRevenue = (BigDecimal) em.createNativeQuery(
                        "SELECT SUM(final_amount) FROM bookings WHERE status IN ('PAID', 'CHECKED_IN')")
                .getSingleResult();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // Tổng số vé đã bán
        Number totalTickets = (Number) em.createNativeQuery(
                        "SELECT COUNT(id) FROM bookings WHERE status IN ('PAID', 'CHECKED_IN')")
                .getSingleResult();
        stats.put("totalTickets", totalTickets != null ? totalTickets.longValue() : 0);

        // Số phim đang chiếu (Giả sử các phim không phải COMING_SOON)
        Number activeMovies = (Number) em.createNativeQuery(
                        "SELECT COUNT(id) FROM movies WHERE status != 'COMING_SOON'")
                .getSingleResult();
        stats.put("activeMovies", activeMovies != null ? activeMovies.longValue() : 0);

        // Tổng khách hàng
        Number totalUsers = (Number) em.createNativeQuery(
                        "SELECT COUNT(id) FROM users")
                .getSingleResult();
        stats.put("totalUsers", totalUsers != null ? totalUsers.longValue() : 0);

        result.put("stats", stats);

        // ==========================================
        // 2. TOP PHIM (Theo số lượng vé)
        // ==========================================
        List<Map<String, Object>> topMovies = new ArrayList<>();
        String topMoviesSql = "SELECT m.title, COUNT(b.id) as tickets " +
                "FROM bookings b " +
                "JOIN showtimes s ON b.showtime_id = s.id " +
                "JOIN movies m ON s.movie_id = m.id " +
                "WHERE b.status IN ('PAID', 'CHECKED_IN') " +
                "GROUP BY m.id, m.title ORDER BY tickets DESC LIMIT 4";
        List<Object[]> topMoviesRaw = em.createNativeQuery(topMoviesSql).getResultList();
        for (Object[] row : topMoviesRaw) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("tickets", ((Number) row[1]).longValue());
            topMovies.add(map);
        }
        result.put("topMovies", topMovies);

        // ==========================================
        // 3. TOP RẠP (Theo doanh thu)
        // ==========================================
        List<Map<String, Object>> topCinemas = new ArrayList<>();
        String topCinemasSql = "SELECT c.name, SUM(b.final_amount) as revenue " +
                "FROM bookings b " +
                "JOIN showtimes s ON b.showtime_id = s.id " +
                "JOIN halls h ON s.hall_id = h.id " +
                "JOIN cinemas c ON h.cinema_id = c.id " +
                "WHERE b.status IN ('PAID', 'CHECKED_IN') " +
                "GROUP BY c.id, c.name ORDER BY revenue DESC LIMIT 4";
        List<Object[]> topCinemasRaw = em.createNativeQuery(topCinemasSql).getResultList();
        for (Object[] row : topCinemasRaw) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("revenue", row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO);
            topCinemas.add(map);
        }
        result.put("topCinemas", topCinemas);

        // ==========================================
        // 4. BIỂU ĐỒ DOANH THU THEO THỜI GIAN
        // ==========================================
        Map<String, List<Map<String, Object>>> revenueDataByTime = new HashMap<>();

        // Cấu hình các câu lệnh SQL theo Hôm nay, Tuần, Tháng, Năm
        revenueDataByTime.put("TODAY", executeRevenueQuery(em,
                "SELECT DATE_FORMAT(created_at, '%H:00') as name, SUM(final_amount) as revenue FROM bookings WHERE status IN ('PAID', 'CHECKED_IN') AND DATE(created_at) = CURDATE() GROUP BY DATE_FORMAT(created_at, '%H:00') ORDER BY name"));

        revenueDataByTime.put("WEEK", executeRevenueQuery(em,
                "SELECT DATE_FORMAT(created_at, '%d/%m') as name, SUM(final_amount) as revenue FROM bookings WHERE status IN ('PAID', 'CHECKED_IN') AND created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY DATE(created_at), DATE_FORMAT(created_at, '%d/%m') ORDER BY DATE(created_at)"));

        revenueDataByTime.put("MONTH", executeRevenueQuery(em,
                "SELECT DATE_FORMAT(created_at, '%d/%m') as name, SUM(final_amount) as revenue FROM bookings WHERE status IN ('PAID', 'CHECKED_IN') AND created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) GROUP BY DATE(created_at), DATE_FORMAT(created_at, '%d/%m') ORDER BY DATE(created_at)"));

        revenueDataByTime.put("YEAR", executeRevenueQuery(em,
                "SELECT DATE_FORMAT(created_at, 'Tháng %m') as name, SUM(final_amount) as revenue FROM bookings WHERE status IN ('PAID', 'CHECKED_IN') AND YEAR(created_at) = YEAR(CURDATE()) GROUP BY MONTH(created_at), DATE_FORMAT(created_at, 'Tháng %m') ORDER BY MONTH(created_at)"));

        result.put("revenueDataByTime", revenueDataByTime);

        return result;
    }

    // Hàm phụ trợ để chạy SQL nhanh cho Biểu đồ
    private List<Map<String, Object>> executeRevenueQuery(EntityManager em, String sql) {
        List<Object[]> raw = em.createNativeQuery(sql).getResultList();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("revenue", row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO);
            list.add(map);
        }
        return list;
    }
}