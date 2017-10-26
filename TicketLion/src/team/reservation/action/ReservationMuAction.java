package team.reservation.action;


import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import team.member.db.memberBean;
import team.member.db.memberDAO;
import team.musical.db.MusicalBean;
import team.musical.db.MusicalDAO;
import team.place.db.PlaceBean;
import team.place.db.PlaceDAO;
import team.playing.db.PlayingBean;
import team.playing.db.PlayingDAO;
import team.reservation.db.Action;
import team.reservation.db.ActionForward;

public class ReservationMuAction implements Action{

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//날짜, 회차, 좌석등급, 잔여석
		String munum = request.getParameter("munum");
		 if(request.getSession().getAttribute("member_num") == null){
             response.setContentType("text/html; charset=UTF-8"); 
             PrintWriter out = response.getWriter();
             out.println("<script>");
             out.println("alert('로그인 해주세요.');");
             out.println("</script>");
             out.close();

             return null;

          
          }
		int mnum = Integer.parseInt(request.getSession().getAttribute("member_num").toString());
		
	    MusicalDAO musicalDao = new MusicalDAO();
	    MusicalBean mub = musicalDao.selectMusical(munum);
	    
	    PlaceDAO placeDao = new PlaceDAO();
	    PlaceBean pb = (PlaceBean)placeDao.getPlayingPlace("musical", munum);
	    
	    memberDAO memberDao = new memberDAO();
	    memberBean mb = memberDao.getmember(mnum);
	    
	    PlayingDAO playingDao = new PlayingDAO();
	    Map<String, Object> pym = (Map<String, Object>) playingDao.getPlayingInfo("musical",munum);
	    
	    request.setAttribute("musical", mub);
	    request.setAttribute("place", pb);
	    request.setAttribute("member", mb);
	    request.setAttribute("playing", pym);
	    
	    ActionForward forward=new ActionForward();
	    forward.setRedirect(false);
	    forward.setPath("/reservation/reservationMU.jsp");
	   
		return forward;
	}
	

}
