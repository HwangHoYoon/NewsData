package com.news.newsdata.common.repository;

import com.news.newsdata.common.entity.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

public interface LogRepository extends JpaRepository<ApiLog, Long> {

    @Procedure("api_log_save")
    void apiLogSave(Long p_user_id, String p_api_url, String p_req_data, String p_res_data, String p_message);

    @Procedure("login_log_save")
    void loginLogSave(Long p_user_id, String p_message);

    @Procedure("activity_log_save")
    void activityLogSave(Long p_user_id, Integer p_log_type, String p_msg);
}
