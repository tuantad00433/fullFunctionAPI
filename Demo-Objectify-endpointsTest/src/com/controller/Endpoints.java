package com.controller;

import com.entity.Credential;
import com.entity.Member;
import com.entity.Student;
import com.google.appengine.repackaged.com.google.common.base.StringUtil;
//import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class Endpoints extends HttpServlet {
    static {
        ObjectifyService.register(Student.class);
        ObjectifyService.register(Member.class);
        ObjectifyService.register(Credential.class);

    }
   static TimerResetToken timerResetToken;
//GET LIST, GET A STUDENT, LOG OUT
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String URI = req.getRequestURI();
        String URISplit[] = URI.split("/");
        String id = URISplit[URISplit.length - 1];
        Gson gson = new Gson();
        if (URISplit.length == 4) {
            List<Student> list = ofy().load().type(Student.class).list();

            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(list));
        } else if (!id.equals("logout")) {
            Key<Student> idKey = Key.create(Student.class, id);
            Student student = ofy().load().key(idKey).now();
            gson = new Gson();
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(student));
        }else if(id.equals("logout")){
            String tokenKey = req.getHeader("Authentication");
            Credential credential = ofy().load().type(Credential.class).filter("tokenKey",tokenKey).first().now();
            ofy().delete().entity(credential).now();
        }
    }

//    REGISTER, LOG IN
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uriSplit[] = req.getRequestURI().split("/");
        String endpoint = uriSplit[uriSplit.length - 1];
        BufferedReader reader = req.getReader();
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        Gson gson = new Gson();

        if (endpoint.equals("register")) {
            try {
                Member member = gson.fromJson(buffer.toString(), Member.class);
//                <---Thiếu Validate Username trùng------------------------>

//                .......................................................
                member.setUserId(String.valueOf(System.currentTimeMillis()));
                ofy().save().entity(member).now();
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("MAYBE WRONG FORMAT?? CHECK INPUT VALUE");
                e.printStackTrace();
            }
        } else if (endpoint.equals("login")) {
            try {
                Member member = new Member();
                member = gson.fromJson(buffer.toString(), Member.class);
                Member memberCheck = ofy().load().type(Member.class).filter("userName", member.getUserName()).first().now();
                if (member.getPassword().equals(memberCheck.getPassword())) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    Credential credential = new Credential();
                    credential.setUserId(memberCheck.getUserId());
                    credential.setTokenKey(UUID.randomUUID().toString());
                    resp.getWriter().write(credential.getTokenKey());
                    ofy().save().entity(credential).now();
                    timerResetToken = new TimerResetToken(credential);
                } else {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
            } catch (Exception e) {
                resp.setStatus((HttpServletResponse.SC_NOT_ACCEPTABLE));
            }
        }


    }
//DELETE STUDENT
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String URI = req.getRequestURI();
        String URISplit[] = URI.split("/");
        String id = URISplit[URISplit.length - 1];
        Key<Student> keyStudent = Key.create(Student.class, id);
        Student student = ofy().load().key(keyStudent).now();
        student.setRollNumber("Da Xoa");
        ofy().save().entity(student).now();
    }

//    EDIT STUDENT
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        System.out.println(buffer.toString());
        Gson gson = new Gson();
        String uri = req.getRequestURI();
        String uriSplit[] = uri.split("/");
        String id = uriSplit[uriSplit.length - 1];
        Key<Student> keyStudent = Key.create(Student.class, id);
        try {
            Student oldStudent = ofy().load().key(keyStudent).now();
            if (oldStudent != null) {
                Student newStudent = gson.fromJson(buffer.toString(), Student.class);
                ofy().save().entity(newStudent).now();
            } else {
                resp.getWriter().write("Student Not Found!");
                resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("MAYBE WRONG FORMAT?? CHECK INPUT VALUE");
        }

    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}
