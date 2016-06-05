package com.mokabuu.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.commons.dbutils.DbUtils;

@Named
@RequestScoped
@ManagedBean(name = "bandDao")
public class BandDao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1804569931056271989L;
//	private final static String INSERT = "insert into fantasia (bandname, email, appdate) values(?,?,now())";
	private final static String SELECT_ALL = "select bandname, date_format(appdate,'%m/%e %H:%i') from fantasia order by appdate";
	
	private String bandname;
	private String email;
	private ArrayList<String> appList = new ArrayList<String>();
	private String appInfo = "";
	protected Connection connection = null;
	
	public String getBandname() {
		return bandname;
	}
	public void setBandname(String bandname) {
		this.bandname = bandname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}	
	public String getAppInfo() {
		return appInfo;
	}
	public void setAppInfo(String appInfo) {
		this.appInfo = appInfo;
	}
	
	public void connect() throws SQLException {
		String url = "jdbc:mysql://localhost/my_db";
		String user = "";
		String password = "";

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException:" + e.getMessage());
		} catch (SQLException e) {
			System.out.println("SQLException:" + e.getMessage());
		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage());
		}
	}
	
	FacesContext context = FacesContext.getCurrentInstance();

	public void register() throws SQLException {
		connect();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("INSERT INTO fantasia (bandname, email, appdate) values (?, ?, CURTIME())");
			ps.setString(1, bandname);
			ps.setString(2, email);
			int isSuccess = ps.executeUpdate();
			if(isSuccess > 0){
				context.addMessage(null, new FacesMessage("【成功】" + bandname + "さんのFBを受け付けました。", "【成功】" + bandname + "さんのFBを受け付けました。"));
			}
		} finally {
			DbUtils.closeQuietly(connection, ps, rs);
		}
		getAppList();
	}
	
	public void getAppList() throws SQLException {
		connect();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(SELECT_ALL);
			rs = ps.executeQuery();
			while(rs.next()){
				appList.add(rs.getString(1)+"("+rs.getString(2)+")");
			}
		} finally {
			DbUtils.closeQuietly(connection, ps, rs);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<ol>");
		for(String tempInfo : appList){
			sb.append("<li>");
			sb.append(tempInfo);
			sb.append("</li>");
		}
		sb.append("</ol>");
		appInfo = sb.toString();
	}
}
