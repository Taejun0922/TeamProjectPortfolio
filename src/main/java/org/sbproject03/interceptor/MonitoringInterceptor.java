package org.sbproject03.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
public class MonitoringInterceptor implements HandlerInterceptor {
//  쓰레드 영역에 변수를 설정하여 특정 쓰레드가 실행되는 모든 코드에서 설정한 변수를 사용할 수 있도록 함
  ThreadLocal<StopWatch> stopWatchThreadLocal = new ThreadLocal<>();
//  요청이 컨트롤러에 들어가기 전에 실행
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    StopWatch stopWatch = new StopWatch(handler.toString());
    stopWatch.start(handler.toString());
    stopWatchThreadLocal.set(stopWatch);
    log.info("접근한 URL 경로 : " + getURLPath(request));
    log.info("요청 처리 시작 시각 : " + getCurrentTime());
    return true;
  }

//  컨트롤러에서 요청에 대한 처리가 끝난 후 실행 (뷰를 실행하기 전)
  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    log.info("요청 처리 종료 시각 : " + getCurrentTime());
  }

//  컨트롤러에서 요청에 대한 처리가 끝나고, 뷰로 응답하고 난 후 실행
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    StopWatch stopWatch = stopWatchThreadLocal.get();
    stopWatch.stop();
    log.info("요청 처리 소요 시간 : " + stopWatch.getTotalTimeMillis() + " ms" );
    stopWatchThreadLocal.remove();
    log.info("-------------------------------------------");

  }

//  요청 URL을 리턴하는 메서드
  private String getURLPath(HttpServletRequest request) {
    String currentPath = request.getRequestURI();
    String queryString = request.getQueryString();
    queryString = queryString == null ? "" : "?" + queryString;
    return currentPath + queryString;
  }

//  요청 URL을 처리하는 날짜와 시간을 리턴하는 메서드
  private String getCurrentTime() {
    DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    return formatter.format(calendar.getTime());
  }
}
