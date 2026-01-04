package com.news.newsdata.tmp2;

public class SalienceCalculator {

    // 클리핑 + 증감 제한 + 정규화
    public static AxesDTO adjustAxes(AxesDTO previous, AxesDTO current) {
        double limit = 0.25;
        AxesDTO adjusted = new AxesDTO(
                clipChange(previous.getAuthority(), current.getAuthority(), limit),
                clipChange(previous.getUrgency(), current.getUrgency(), limit),
                clipChange(previous.getLinkTrust(), current.getLinkTrust(), limit),
                clipChange(previous.getNoCallback(), current.getNoCallback(), limit)
        );
        // 정규화: 합이 3 이상이면 비율 축소
        double sum = adjusted.axisSum();
        if(sum > 3.0) adjusted.scale(3.0/sum);
        adjusted.clip();
        return adjusted;
    }

    private static double clipChange(double prev, double curr, double limit) {
        double diff = curr - prev;
        if(diff > limit) return prev + limit;
        if(diff < -limit) return prev - limit;
        return curr;
    }

    public static double calculateSalience(AxesDTO axes, int distanceFromLatest, String verdict) {
        double recency = 1.0 / (1 + distanceFromLatest);
        double error = ("risky".equals(verdict) || "unsafe".equals(verdict)) ? 1.0 : 0.0;
        double axisMax = axes.axisMax();
        double salience = 0.4*recency + 0.4*error + 0.2*axisMax;
        return Math.min(1.0, Math.max(0.0, salience));
    }
}
