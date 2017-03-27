package com.predictext.servlets;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bbva on 27/03/17.
 */
// The Enqueue servlet should be mapped to the "/enqueue" URL.
public class ImportTask extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        // Add the task to the default queue.
        Queue queue = QueueFactory.getQueue("import");
        queue.add(TaskOptions.Builder.withUrl("/import"));

        response.getWriter().write("Taks launched");
    }

}
