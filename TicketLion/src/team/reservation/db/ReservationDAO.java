package team.reservation.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.mysql.jdbc.Statement;

import team.movie.db.MovieBean;
import team.musical.db.MusicalBean;

public class ReservationDAO {
	//여러가지 테이블을 건드려서 따로 만들긴 했는데 원래 DAO로 돌려놔야할지 고려할 것
	private Connection getConnection() throws Exception{
		Connection con=null;
		Context init=new InitialContext();
		DataSource ds=(DataSource)init.lookup("java:comp/env/jdbc/Ticketlion");
		con=ds.getConnection();
		return con;
	}
	
	public List<Map<String, Object>> getPlace(){
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		List<Map<String, Object>> placeList = new ArrayList<>();
		try{
			con=getConnection();
			
			sql="select a.p_code, b.l_code , c.t_code, a.name from place a, location b, theater c "
					+ "where substring(p_code, 1, 2) = b.l_code and substring(a.p_code, 7,2) = c.t_code";
			pstmt=con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				do{
					Map<String, Object> resultMap = new HashMap<>();
					
					resultMap.put("p_code", rs.getString("p_code"));
					resultMap.put("l_code", rs.getString("l_code"));
					resultMap.put("t_code", rs.getString("t_code"));
					resultMap.put("name", rs.getString("name"));
					
					placeList.add(resultMap);
				}while(rs.next());
			}
			
			return placeList;
			
		}catch(Exception e){
			System.out.println("ReservDAO getplace error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
		return null;
	}
	
	//예매 화면에서 movie_num 또는 date 넘겨줬을때 사용
	public List<Map<String, Object>> getPlace(String flag, String value ){
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		List<Map<String, Object>> pcodeList = new ArrayList<>();
		
		System.out.println(flag+ value);
		try{
			con=getConnection();
			
			if(flag.equals("movie")){
				sql="select p_code from playing where nc_code = concat('mo',?) ";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, value);
				rs = pstmt.executeQuery();
				System.out.println(pstmt.toString());
				if(rs.next()){
					do{
						Map<String, Object> resultMap = new HashMap<>();
						
						resultMap.put("p_code", rs.getString("p_code"));
						pcodeList.add(resultMap);
					}while(rs.next());
				}
				return pcodeList;
			} else if(flag.equals("date")){
				System.out.println(value);
				String d = value.substring(0, 4)+"-"+value.substring(4,6)+"-"+value.substring(6);
				System.out.println(d);
				sql="select p_code from playing where start_day <= date_format(now(), '%Y%m%d') and end_day >= date_format(?,'%Y%m%d');";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, d);
				rs = pstmt.executeQuery();
				System.out.println(pstmt.toString());
				if(rs.next()){
					do{
						Map<String, Object> resultMap = new HashMap<>();
						
						resultMap.put("p_code", rs.getString("p_code"));
						pcodeList.add(resultMap);
					}while(rs.next());
				}
				
				return pcodeList;
			}else if(flag.equals("all")){
				System.out.println(value);
				
				String movie_num = value.substring(0,8);
				String day = value.substring(8, 12)+"-"+value.substring(12,14)+"-"+value.substring(14);
				
				sql="select p_code from playing where start_day <= date_format(now(), '%Y%m%d') and end_day >= date_format(?,'%Y%m%d') and nc_code = concat('mo',?)";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, day);
				pstmt.setString(2, movie_num);
				rs = pstmt.executeQuery();
				System.out.println(pstmt.toString());
				if(rs.next()){
					do{
						Map<String, Object> resultMap = new HashMap<>();
						
						resultMap.put("p_code", rs.getString("p_code"));
						pcodeList.add(resultMap);
					}while(rs.next());
				}
				
				return pcodeList;
			}
		}catch(Exception e){
			System.out.println("ReservDAO getplace error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
		return null;
	}
	
	//예매 화면에서 theater 또는 date 넘겨줬을때 사용
	public List<Map<String, Object>> getMovie(String flag, String value ){
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		List<Map<String, Object>> monumList = new ArrayList<>();
		
		System.out.println(flag+ value);
		try{
			con=getConnection();
			
			//상영관 기준(p_code)으로 상영하는 영화 번호 목록 가져오기
			if(flag.equals("theater")){
				sql="select substring(nc_code, 3) as movie_num from playing where p_code = ? ";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, value);
				rs = pstmt.executeQuery();
				System.out.println(pstmt.toString());
				if(rs.next()){
					do{
						Map<String, Object> resultMap = new HashMap<>();
						
						resultMap.put("movie_num", rs.getString("movie_num"));
						monumList.add(resultMap);
					}while(rs.next());
				}
				
				return monumList;
			} else if(flag.equals("date")){
				//날짜 기준으로 상영하는 영화 번호 목록 가져오기
				String day = value.substring(0, 4)+"-"+value.substring(4,6)+"-"+value.substring(6);
				sql="select distinct substring(nc_code, 3) as movie_num from playing where start_day <= date_format(now(), '%Y%m%d') and end_day >= date_format(?,'%Y%m%d')";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, day);
				rs = pstmt.executeQuery();
				System.out.println(pstmt.toString());
				if(rs.next()){
					do{
						Map<String, Object> resultMap = new HashMap<>();
						System.out.println(rs.getString("movie_num"));
						resultMap.put("movie_num", rs.getString("movie_num"));
						monumList.add(resultMap);
					}while(rs.next());
				}
				
				return monumList;
			} else if(flag.equals("all")){
				String pcode = value.substring(0,8);
				String day = value.substring(8, 12)+"-"+value.substring(12,14)+"-"+value.substring(14);
				
				sql="select substring(nc_code, 3) as movie_num from playing where p_code = ? and start_day <= date_format(now(), '%Y%m%d') and end_day >= date_format(?,'%Y%m%d')";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, pcode);
				pstmt.setString(2, day);
				rs = pstmt.executeQuery();
				System.out.println(pstmt.toString());
				if(rs.next()){
					do{
						Map<String, Object> resultMap = new HashMap<>();
						System.out.println(rs.getString("movie_num"));
						resultMap.put("movie_num", rs.getString("movie_num"));
						monumList.add(resultMap);
					}while(rs.next());
				}
				return monumList;
			}
		}catch(Exception e){
			System.out.println("ReservDAO getMovie error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
		return null;
	}

	public MovieBean getMovieInfo(String mo_num){
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		MovieBean mb = new MovieBean();
		
		try{
			con=getConnection();
			
			sql="select a.name, a.age, b.image from movie a, movie_detail b where a.movie_num= ? and a.movie_num = b.movie_num ";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, mo_num);
			rs = pstmt.executeQuery();
			System.out.println(pstmt.toString());
			if(rs.next()){
				do{
					mb.setImage(rs.getString("image"));
					mb.setAge(rs.getString("age"));
					mb.setName(rs.getString("name"));
					
				}while(rs.next());
			}
			
			return mb;
			
		}catch(Exception e){
			System.out.println("ReservDAO getMovieInfo error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
		return null;
	}
	
	
	public List<Map<String, Object>> getLocation(){
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		List<Map<String, Object>> locationList = new ArrayList<>();
		try{
			con=getConnection();
			
			sql="select a.l_code, a.location, count(*) as count, a.idx from location a, place b "
					+ "where substring(b.p_code, 1,2) = a.l_code group by substring(b.p_code , 1, 2) order by a.idx";
			
			
			for(int i=1; i<=9 ; i++){
				sql = "select group_concat(a.l_code separator '_') as l_code , group_concat(a.location separator '/') as location, sum(a.count) as count, a.idx " 
						+ "from ( select a.l_code, a.location, count(*) as count, a.idx from location a, place b " 
						+ "where substring(b.p_code, 1,2) = a.l_code group by a.l_code order by a.idx ) a where idx = ?" ;
				pstmt=con.prepareStatement(sql);
				pstmt.setInt(1, i);
				
				rs = pstmt.executeQuery();
				
				if(rs.next()){
					do{
						Map<String, Object> resultMap = new HashMap<>();
						
						resultMap.put("l_code", rs.getString("l_code"));
						resultMap.put("location", rs.getString("location"));
						resultMap.put("count", rs.getString("count"));
						resultMap.put("index", rs.getInt("idx"));
						
						locationList.add(resultMap);
					}while(rs.next());
				}
			}

			for(int j=0; j<9; j++){
				System.out.println(locationList.get(j).get("location"));
			}
			
			return locationList;
			
		}catch(Exception e){
			System.out.println("ReservDAO getlocation error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
		return null;
	}
	
	//reservation 첫 화면에서 날짜 정보 조회
	public List<Map<String, Object>> getPlayDay() throws Exception{
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		List<Map<String, Object>> playdayList = new ArrayList<>();
		try{
			con=getConnection();
			
			sql="select distinct date_format(play_day, '%Y%m%d') as pday, year(play_day) as year, month(play_day) as month, dayofmonth(play_day) as day, dayname(play_day) as dayname "
					+ "from playtime where play_day >= date_format(now(), '%Y%m%d')";
			pstmt=con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if(rs.next()){
				do{
					Map<String, Object> playday = new HashMap<>();
					playday.put("pday",rs.getString("pday"));
					playday.put("year", rs.getString("year"));
					playday.put("month", rs.getString("month"));
					playday.put("day", rs.getString("day"));
					playday.put("dayname", rs.getString("dayname"));
					playdayList.add(playday);
				}while(rs.next());
			}
			return playdayList;
					
		}catch(Exception e){
			System.out.println("ReserDAO selectPlayday error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return null;
	}
	
	// 특정 영화나 상영관 선택시 해당하는 영화 상영일자 조회
	public String[] getPlayDay(String flag, String value) throws Exception{
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		
		try{
			con=getConnection();
			
			
			if(flag.equals("theater")){
			
				System.out.println(flag + value);
				
				sql=" select min(start_day), max(end_day) from playing where p_code = ?";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, value);
				rs = pstmt.executeQuery();
				rs.next();
				
				try{
					if(rs.getString(1).isEmpty()){
						//모든 상영관이 표시되기 때문에 상영하는 영화가 존재하지 않을 수 있음
					}else{
						String[] duration = {rs.getString(1), rs.getString(2)};
						return duration;
					}
				}catch(NullPointerException e){
					e.getMessage();
					String[] duration = {"n", "n"};
					return duration;
				}
			}else if(flag.equals("movie")){
				sql="select distinct start_day, end_day from playing where nc_code = concat('mo',?)";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, value);
				rs = pstmt.executeQuery();
				
				rs.next();
				String[] duration = {rs.getString(1), rs.getString(2)};
				return duration;
			}else if(flag.equals("all")){
				String pcode = value.substring(0, 8);
				String movie_num = value.substring(8);
				sql="select start_day, end_day from playing where nc_code = concat('mo', ?) and p_code = ?";
				pstmt=con.prepareStatement(sql);
				pstmt.setString(1, movie_num);
				pstmt.setString(2, pcode);
				rs = pstmt.executeQuery();
				
				rs.next();
				System.out.println(pstmt.toString());
				String[] duration = {rs.getString(1), rs.getString(2)};
				return duration;
			}
					
		}catch(Exception e){
			System.out.println("ReserDAO selectPlayday(theater, movie) error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return null;
	}
	
	public List<Map<String, Object>> getTimeMo(String mo_num, String pcode, String playday) throws Exception{
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		List<Map<String, Object>> TimeInfoList = new ArrayList<>();
		
		try{
			con=getConnection();
			
			sql = " select distinct a.*, a.capacity - coalesce(b.reserved,0) as remained "
		               +"from (select * from v_playinfo where substring(nc_code, 3) = ? and play_day = ?) a "
		               +"left join v_reserved_seatinfo b on b.view_date = concat(a.play_day, ' ', a.ptime, ':00') "
		               +"and a.ping_num = b.ping_num and a.screen_name = b.screen_name "
		               +"order by play_day, screen_name, ptime";
			
			pstmt=con.prepareStatement(sql);
			
			String day = playday.substring(0, 4)+"-"+playday.substring(4,6)+"-"+playday.substring(6);
		     pstmt.setString(1, mo_num);
		       pstmt.setString(2, day);
			/*pstmt.setString(1, day);
			pstmt.setString(2, pcode);
			pstmt.setString(3, mo_num);*/
			rs = pstmt.executeQuery();
			
			System.out.println(pstmt.toString());
			
			if(rs.next()){
				do{
					Map<String, Object> timeInfo = new HashMap<>();
					timeInfo.put("screen_name", rs.getString("screen_name"));
					timeInfo.put("ptime", rs.getString("ptime"));
					timeInfo.put("capacity", rs.getString("capacity"));
					timeInfo.put("remained", rs.getString("remained"));
					TimeInfoList.add(timeInfo);
				}while(rs.next());
			}
			return TimeInfoList;
		}catch(Exception e){
			System.out.println("ReserDAO getTimeInfo error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return null;
	}
	
	//reservation 첫 화면에서 날짜 정보 조회
	public Map<String, Object> getSeatMap(String pcode) throws Exception{
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		Map<String, Object> resultMap = new HashMap<>();
		
		try{
			con=getConnection();
			
			sql="select totalrowcount, rowgroupcount, rowgroupseatcount, colgroupcount, colgrouprowcount, vipscope, rscope, sscope, vacantseat "
					+ "from seatmap where p_code = ?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, pcode);
			rs = pstmt.executeQuery();

			if(rs.next()){
				do{
					resultMap.put("totalrowcount", rs.getInt("totalrowcount"));
					resultMap.put("rowgroupcount", rs.getInt("rowgroupcount"));
					resultMap.put("rowgroupseatcount", rs.getString("rowgroupseatcount"));
					resultMap.put("colgroupcount", rs.getInt("colgroupcount"));
					resultMap.put("colgrouprowcount", rs.getString("colgrouprowcount"));
					resultMap.put("vipscope", rs.getString("vipscope"));
					resultMap.put("rscope", rs.getString("rscope"));
					resultMap.put("sscope", rs.getString("sscope"));
					resultMap.put("vacantseat", rs.getString("vacantseat"));
				}while(rs.next());
			}
			return resultMap;
					
		}catch(Exception e){
			System.out.println("ReserDAO selectPlayday error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return null;
	}
	
	public int getPingnum(String pcode, String mnum, String flag) throws Exception{
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		int pingnum = 0;
		
		try{
			con=getConnection();
			
			if(flag == "mo"){
				sql="select ping_num from playing where p_code = ? and nc_code = concat('mo',?) ";
			}else if(flag == "mu"){
				sql="select ping_num from playing where p_code = ? and nc_code = concat('mu',?) ";
			}
			
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, pcode);
			pstmt.setString(2,mnum);
			rs = pstmt.executeQuery();

			if(rs.next()){
				pingnum = rs.getInt(1);
			}
			return pingnum;
					
		}catch(Exception e){
			System.out.println("ReserDAO getpingnum error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return 0;
	}
	
	public int insertReservation(ReservationBean rsb) throws Exception{
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		int renum = 0;
		
		try{
			con=getConnection();
			
			sql="insert into reservation "
					+ "(member_num, ping_num, rseat_num, seat, view_date, reser_day, pay_day, screen_name, mPoint, price, payinfo) "
					+ "values ( ?,?,?,?,?,now(),now(),?,?,?,?)";
			pstmt=con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, rsb.getMember_num());
			pstmt.setInt(2, rsb.getPing_num());
			pstmt.setString(3, rsb.getReseat_num());
			pstmt.setString(4, rsb.getSeat());
			pstmt.setString(5, rsb.getView_date());
			pstmt.setString(6, rsb.getScreen_name());
			pstmt.setInt(7, rsb.getMPoint());
			pstmt.setInt(8,  rsb.getPrice());
			pstmt.setString(9,  rsb.getPayinfo());
			
			pstmt.executeUpdate();
			
			/*	rs = pstmt.getGeneratedKeys();
			System.out.println(pstmt.toString());
			*/
			sql="select r_num from reservation where member_num=? and ping_num=?";
			pstmt= con.prepareStatement(sql);
			pstmt.setInt(1, rsb.getMember_num());
			pstmt.setInt(2, rsb.getPing_num());
			rs=pstmt.executeQuery();
			
			if(rs.next()){
				renum = rs.getInt(1);
				return renum;
			}
			
		}catch(Exception e){
			System.out.println("ReserDAO insertReservation error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return 0;
	}
	
	public ReservationBean insertReservedSeat(ReservationBean rsb) throws Exception{
		Connection con= null;
		PreparedStatement pstmt = null;
		PreparedStatement nextpstmt = null;
		String sql="";
		String nextSql = "";
		ResultSet rs = null;
		ReservationBean reRsb = new ReservationBean();
		int renum = 0;
		
		
		try{
			con=getConnection();
			
			int i=0;
			int result=0;

			//몇명을 예매했는지 count 구해오기

			while(i<(rsb.getReseat_num()).length()) {
				
				   if((rsb.getReseat_num()).charAt(i)>=47 &&  (rsb.getReseat_num()).charAt(i)<=58) {
				    result += Integer.parseInt((rsb.getReseat_num()).substring(i,i+1));
					
				   }
				   i++;
			}
			
			String[] seats = rsb.getSeat().split(" ");
			
			for(int s=0; s<result; s++){
				nextpstmt = null;
				nextSql = "insert into reserved_seat (r_num, ping_num, screen_name, seat, view_date)"+
						"values (?,?,?,?,?)";
				nextpstmt = con.prepareStatement(nextSql);
				nextpstmt.setInt(1, rsb.getR_num());
				nextpstmt.setInt(2, rsb.getPing_num());
				nextpstmt.setString(3, rsb.getScreen_name());
				nextpstmt.setString(4, seats[s]);
				nextpstmt.setString(5, rsb.getView_date());
				
				nextpstmt.executeUpdate();
			}

			
			
			reRsb = rsb;

			return reRsb;
			
					
		}catch(Exception e){
			System.out.println("ReserDAO insertReservation error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(nextpstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return null;
	}
	
	public int insertReservedSeat(ReservationBean rsb, String grade) throws Exception{
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		int re = 0;
		
		
		try{
			con=getConnection();
			
			int rseatnum = Integer.parseInt(rsb.getReseat_num());
			//몇명을 예매했는지 count 구해오기

			String[] seats = rsb.getSeat().split(" ");
			String[] grades = grade.split(" ");
			for(int s=0; s<rseatnum; s++){
				
				sql = "insert into reserved_seat (r_num, ping_num, screen_name, seatgrade, seat, view_date)"+
						"values (?,?,?,?,?,?)";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, rsb.getR_num());
				pstmt.setInt(2, rsb.getPing_num());
				pstmt.setString(3, rsb.getScreen_name());
				pstmt.setString(4, grades[s]);
				pstmt.setString(5, seats[s]);
				pstmt.setString(6, rsb.getView_date());
				
				re = pstmt.executeUpdate();
			}

			return re;
			
					
		}catch(Exception e){
			System.out.println("ReserDAO insertReservesSeat Mu error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return 0;
	}
	
	public List<Map<String, Object>> getReservedSeats(int pingnum, String screen_name, String viewdate) throws Exception{
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		List<Map<String, Object>> rvedseatList = new ArrayList<>();
		
		try{
			con=getConnection();
			
			sql="select seat from reserved_seat where ping_num = ? and screen_name = ? and view_date = ?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, pingnum);
			pstmt.setString(2, screen_name);
			pstmt.setString(3, viewdate);
			rs = pstmt.executeQuery();
			
			System.out.println(pstmt.toString());
			if(rs.next()){
				do{
					Map<String, Object> resultmap = new HashMap<>();
					String seat = rs.getString("seat");
					resultmap.put("seat_fl", seat.substring(0,1));
					resultmap.put("seat_no", seat.substring(1));
					rvedseatList.add(resultmap);
				}while(rs.next());
			}
			
			return rvedseatList;
					
		}catch(Exception e){
			System.out.println("ReserDAO reservedseat error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return null;
	}
	
	public List<Map<String, Object>> getReservedSeats(int pingnum, String viewdate) throws Exception{
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		List<Map<String, Object>> rvedseatList = new ArrayList<>();
		
		try{
			con=getConnection();
			
			sql="select seat from reserved_seat where ping_num = ? and view_date = ?";
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, pingnum);
			pstmt.setString(2, viewdate);
			rs = pstmt.executeQuery();
			
			System.out.println(pstmt.toString());
			if(rs.next()){
				do{
					Map<String, Object> resultmap = new HashMap<>();
					String seat = rs.getString("seat");
					resultmap.put("seat_id", seat.substring(0,8));
					resultmap.put("seat_Bl", seat.substring(8));
					
					System.out.println(seat.substring(0,8) + " " + seat.substring(8));
					rvedseatList.add(resultmap);
				}while(rs.next());
			}
			
			
			return rvedseatList;
					
		}catch(Exception e){
			System.out.println("ReserDAO reservedseat error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return null;
	}
	
	
	public List<MovieBean> getOrderReservMo(){
		ArrayList<MovieBean> reservList = new ArrayList<MovieBean>();
		Connection con = null;
		PreparedStatement pstmt =null;
		ResultSet rs = null;
		try{
			con =getConnection();
			String sql="select m.name,"+
					   " round((select count(m.movie_num)/"+
					   "(select count(*) from reservation r, playing p, movie m where r.ping_num=p.ping_num"+
					   " and substring(p.nc_code,3) = m.movie_num)*100 from dual),1) as percent, m.age, m.movie_num"+
					   " from reservation r, playing p, movie m"+
					   " where r.ping_num  = p.ping_num"+
					   " and substring(p.nc_code,3) = m.movie_num"+
					   " group by m.movie_num order by 2 desc limit 0,10";
			pstmt= con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			while(rs.next()){
				MovieBean mb = new MovieBean();
				
				mb.setName(rs.getString(1));
				mb.setPercent(rs.getDouble(2));
				mb.setAge(rs.getString("age"));
				mb.setMovie_num(rs.getString("movie_num"));
				reservList.add(mb);
			}
		}catch(Exception e){
			System.out.println("getOrderReservMo  error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return reservList;
	}
	
	public List<MusicalBean> getOrderReservMu(){
		ArrayList<MusicalBean> reservList = new ArrayList<MusicalBean>();
		Connection con = null;
		PreparedStatement pstmt =null;
		ResultSet rs = null;
		try{
			con =getConnection();
			String sql="select m.name, "+
						"round((select count(m.musical_num)/"+
						"(select count(*) from reservation r, playing p, musical m where r.ping_num=p.ping_num"+
						" and substring(p.nc_code,3) = m.musical_num)*100 from dual),1) as percent, m.age, m.musical_num"+
						" from reservation r, playing p, musical m"+
						" where r.ping_num  = p.ping_num"+
						" and substring(p.nc_code,3) = m.musical_num"+
						" group by m.musical_num order by 2 desc limit 0,10";
			pstmt= con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			while(rs.next()){
				MusicalBean mb = new MusicalBean();
				
				mb.setName(rs.getString(1));
				mb.setPercent(rs.getDouble(2));
				mb.setAge(rs.getString("age"));
				mb.setMusical_num(rs.getString("musical_num"));
				reservList.add(mb);
			}
		}catch(Exception e){
			System.out.println("getOrderReservMu  error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return reservList;
	}
	
	public List<Map<String, Object>> getTimeMu(String mu_num, String date) throws Exception{
		Connection con= null;
		PreparedStatement pstmt = null;
		String sql="";
		ResultSet rs = null;
		List<Map<String, Object>> TimeseatInfoList = new ArrayList<>();
		
		try{
			con=getConnection();
			
			sql = "select distinct a.*, a.capacity - coalesce(b.reserved,0) as remained "
					+ "from (select * from v_playmusicalinfo where substring(nc_code, 3) = ? and play_day = ?) a "
					+ "left join v_reserved_seatinfo b on b.view_date = concat(a.play_day, ' ', a.ptime, ':00') and a.ping_num = b.ping_num "
					+ "and a.seatclass = b.seatgrade order by 7,8,field(seatclass, 'VIP', 'R', 'S')";
			
			

			pstmt=con.prepareStatement(sql);
			
			pstmt.setString(1, mu_num);
			pstmt.setString(2, date);
			rs = pstmt.executeQuery();
			
			System.out.println(pstmt.toString());
			
			if(rs.next()){
				do{
					Map<String, Object> timeseatInfo = new HashMap<>();
					timeseatInfo.put("ping_num", rs.getString("ping_num"));
					timeseatInfo.put("p_code", rs.getString("p_code"));
					timeseatInfo.put("ptime", rs.getString("ptime"));
					timeseatInfo.put("seatclass", rs.getString("seatclass"));
					timeseatInfo.put("capacity", rs.getString("capacity"));
					timeseatInfo.put("remained", rs.getString("remained"));
				TimeseatInfoList.add(timeseatInfo);
				}while(rs.next());
			}
			return TimeseatInfoList;
		}catch(Exception e){
			System.out.println("ReserDAO getTimeInfoMu error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return null;
	}
	
	public List<MovieBean> getOrderReservMo2(){
		ArrayList<MovieBean> reservList = new ArrayList<MovieBean>();
		Connection con = null;
		PreparedStatement pstmt =null;
		ResultSet rs = null;
		try{
			con =getConnection();
			String sql="select m.name,"+
					   " round((select count(m.movie_num)/"+
					   "(select count(*) from reservation r, playing p, movie m where r.ping_num=p.ping_num"+
					   " and substring(p.nc_code,3) = m.movie_num)*100 from dual),1) as percent, m.age, m.movie_num"+
					   " from reservation r, playing p, movie m"+
					   " where r.ping_num  = p.ping_num"+
					   " and substring(p.nc_code,3) = m.movie_num"+
					   " group by m.movie_num order by 2 desc limit 0,6";
			pstmt= con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			while(rs.next()){
				MovieBean mb = new MovieBean();
				
				mb.setName(rs.getString(1));
				mb.setPercent(rs.getDouble(2));
				mb.setAge(rs.getString("age"));
				mb.setMovie_num(rs.getString("movie_num"));
				reservList.add(mb);
			}
		}catch(Exception e){
			System.out.println("getOrderReservMo  error : "+e);
		}finally{
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(pstmt!=null){try{pstmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con!=null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
	return reservList;
	}
	
}
