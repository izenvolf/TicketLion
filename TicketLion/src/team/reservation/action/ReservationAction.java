package team.reservation.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import team.movie.db.MovieBean;
import team.movie.db.MovieDAO;
import team.place.db.PlaceDAO;
import team.playing.db.PlayingDAO;
import team.reservation.db.Action;
import team.reservation.db.ActionForward;
import team.reservation.db.ReservationDAO;

public class ReservationAction implements Action{

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//팝업창 호출 위치에 따라 기초 설정값 받기
		String flag = request.getParameter("flag");
		String val = request.getParameter("val");
		//맵으로 저장 후 전달
		Map<String, Object> preChoice = new HashMap<>();
		preChoice.put("flag", flag);
		preChoice.put("val", val);
		
		//예매화면 띄우기
		//상영하는 영화, 영화관, 상영시간
		MovieDAO movieDao = new MovieDAO();
		ReservationDAO reserDao = new ReservationDAO();
		
		//한영화가 상영하는 극장과 관, 상영시간
		List<MovieBean> movieList = movieDao.playingMovies();
		movieList = movieDao.getEvent(movieList);
		List<Map<String, Object>> placeList = reserDao.getPlace();
		List<Map<String, Object>> locationList = reserDao.getLocation();
		List<Map<String, Object>> playdayList = reserDao.getPlayDay();
		
		for(int i=0; i<playdayList.size(); i++){
			String dayname = (String)playdayList.get(i).get("dayname");
			switch(dayname){
			case "Monday":
				playdayList.get(i).put("dayname", "월");
				break;
			case "Tuesday":
				playdayList.get(i).put("dayname", "화");
				break;
			case "Wednesday":
				playdayList.get(i).put("dayname", "수");
				break;
			case "Thursday":
				playdayList.get(i).put("dayname", "목");
				break;
			case "Friday":
				playdayList.get(i).put("dayname", "금");
				break;
			case "Saturday":
				playdayList.get(i).put("dayname", "토");
				break;
			case "Sunday":
				playdayList.get(i).put("dayname", "일");
				break;
			}
		}
		
	    
	    request.setAttribute("movieList", movieList);
	    request.setAttribute("placeList", placeList);
	    request.setAttribute("locationList", locationList);
	    request.setAttribute("playdayList", playdayList);
	    request.setAttribute("preChoice", preChoice);
	    
	    ActionForward forward=new ActionForward();
	    forward.setRedirect(false);
	    forward.setPath("reservation/reservationMV.jsp");
	   
		return forward;
	}
	

}
