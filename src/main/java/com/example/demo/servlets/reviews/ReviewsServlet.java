package com.example.demo.servlets.reviews;

import com.example.demo.Guard;
import com.example.demo.S3;
import com.example.demo.Util;
import com.example.demo.beans.Alert;
import com.example.demo.beans.entities.*;
import com.example.demo.daos.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebServlet(name = "Reviews", value = "/reviews")
public class ReviewsServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)  {
        doRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)  {
        doRequest(request, response);
    }

    public void doRequest(HttpServletRequest request, HttpServletResponse response) {
        String function = request.getParameter("f");

        if (function == null) {
            function = "index";
        }

        try {
            switch (function) {
                case "list":
                    list(request, response);
                    break;
                case "vote":
                    vote(request, response);
                    break;
                case "create":
                    create(request, response);
                    break;
                case "edit":
                    edit(request, response);
                    break;
                case "delete":
                    delete(request, response);
                    break;
                case "hide":
                    hide(request, response);
                    break;
                default:
                    getAll(request, response);
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void vote(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        if (!request.getMethod().equals("POST")) {
            response.getWriter().println("Invalid method");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User user = Guard.requireAuthenticationWithMessage(
                request,
                response,
                "You must be logged in to vote."
        );

        if (user == null) {
            return;
        }

        HttpSession session = request.getSession();

        Long reviewId = Util.parseLongOrNull(request.getParameter("id"));

        if (reviewId == null) {
            session.setAttribute("alert", new Alert("danger", "Invalid review id"));
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        Optional<Review> review = ReviewDao.get(reviewId);

        if (review.isEmpty()) {
            session.setAttribute("alert", new Alert("danger", "Invalid review id" ));
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            session.setAttribute("alert", new Alert("danger", "Invalid vote method"));
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        int vote = switch (action) {
            case "up" -> 1;
            case "down" -> -1;
            default -> 0;
        };

        if (vote == 0) {
            session.setAttribute("alert", new Alert("danger", "Invalid vote method"));
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        ReviewDao.vote(review.get(), user, vote);
        response.sendRedirect(request.getContextPath() + "/reviews?f=list&id=" + review.get().getAmenityId()  + "#review-" + review.get().getId());
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        User user = Guard.requireAuthenticationWithMessage(request, response, "Must be logged in to delete.");
        if (user == null) return;
        Long reviewId = Util.parseLongOrNull(request.getParameter("id"));

        if(reviewId == null){
            response.sendRedirect(request.getContextPath() + "/reviews");
            return;
        };

        Optional<Review> review = ReviewDao.get(reviewId);

        if(!review.isPresent()){
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        if(!user.getId().equals(review.get().getUserId()) ){
            response.sendRedirect(request.getContextPath() + "/reviews");
            return;

        };

        ReviewDao.delete(review.get());
        response.sendRedirect(request.getContextPath() + "/reviews?f=list&id=" + review.get().getAmenityId());

    }

    public void hide(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        User user = Guard.requireAuthenticationWithMessage(request, response, "Must be logged in to hide a review.");
        if (user == null) return;
        Long reviewId = Util.parseLongOrNull(request.getParameter("id"));

        if(reviewId == null){
            response.sendRedirect(request.getContextPath() + "/reviews");
            return;
        };

        Optional<Review> review = ReviewDao.get(reviewId);

        if(!review.isPresent()){
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        if(!(user.isAdministrator() || user.isModerator())){
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        ReviewDao.toggleHide(review.get());
        response.sendRedirect(request.getContextPath() + "/reviews?f=list&id=" + review.get().getAmenityId());

    }

    public void list(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        Long amenityId = Util.parseLongOrNull(request.getParameter("id"));

        if (amenityId == null) {
            response.sendRedirect(request.getContextPath() + "/reviews");
            return;
        }
        User user = (User)request.getAttribute("user");
        List<Review> reviews = ReviewDao.getAllReviews(
                amenityId, (Long) request.getSession().getAttribute("user_id"), user == null ? Boolean.FALSE : user.isAdministrator() || user.isModerator() );

        for (Review review: reviews) {
            review.setMetrics(ReviewDao.getAllReviewMetricRecordsWithNames(review.getId()));
            review.setImages(ReviewDao.getAllImages(review.getId()));
            UserDao.getInstance().get(review.getUserId()).ifPresent(review::setUser);
        }

        request.setAttribute("reviews", reviews);
        request.getRequestDispatcher("/template/reviews/list.jsp").forward(request, response);



    }

    public void edit(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        User user = Guard.requireAuthenticationWithMessage(
                request,
                response,
                "You must be logged in to edit your review."
        );

        if (user == null) {
            return;
        };

        Long reviewId = Util.parseLongOrNull(request.getParameter("id"));

        if(reviewId == null){
            response.sendRedirect(request.getContextPath() + "/reviews");
            return;
        };

        Optional<Review> review = ReviewDao.get(reviewId);

        if(!review.isPresent()){
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        if(!user.getId().equals(review.get().getUserId()) ){
            response.sendRedirect(request.getContextPath() + "/reviews");
            return;
        };

        Optional<Amenity> amenity = AmenityDao.getInstance().get(review.get().getAmenityId());

        if (!amenity.isPresent()){
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        List<AmenityTypeMetricRecordWithName> metrics = ReviewDao.getAllReviewMetricRecordsWithNames(review.get().getId());
        request.setAttribute("metrics", metrics);
        request.setAttribute("headerText", "Edit Review");
        request.setAttribute("submitButtonText", "Save Changes");
        request.setAttribute("editMode", true);


        if (request.getMethod().equals("POST")) {
            String name = request.getParameter("name");
            String description = request.getParameter("description");

            if (name == null || name.isEmpty() || description == null || description.isEmpty()) {
                request.setAttribute("alert", new Alert("danger", "Please fill out all fields."));
                // TODO fix
                request.getRequestDispatcher("/template/reviews/form.jsp").forward(request, response);
                return;
            }

            review.get().setName(name);
            review.get().setDescription(description);
            ReviewDao.update(review.get());

            for (AmenityTypeMetricRecordWithName metric : metrics) {
                int value = Util.parseIntOrDefault(request.getParameter("metric-" + metric.getAmenityMetricId()), 0);

                if (value < 0) {
                    value = 0;
                } else if (value > 5) {
                    value = 5;
                }

                ReviewDao.updateReviewRecord(metric.getReviewId(), metric.getAmenityMetricId(), value);
            }

            response.sendRedirect(request.getContextPath() + "/reviews?f=list&id=" + review.get().getAmenityId() + "#review-" + review.get().getId());
            return;
        } else {
            request.setAttribute("review", review.get());

            request.getRequestDispatcher("/template/reviews/form.jsp").forward(request, response);
        }

    }

    public void create(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        User user = Guard.requireAuthenticationWithMessage(
                request,
                response,
                "You must be logged in to create a review."
        );

        if (user == null) {
            return;
        }

        Long amenityId = Util.parseLongOrNull(request.getParameter("amenityId"));

        if (amenityId == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        Optional<Amenity> amenity = AmenityDao.getInstance().get(amenityId);

        if (!amenity.isPresent()){
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request.setAttribute("headerText", "Create Review");
        request.setAttribute("submitButtonText", "Create");
        request.setAttribute("editMode", false);

        if (request.getMethod().equals("POST")) {
            List<AmenityTypeMetric> metrics = AmenityTypeMetricDao.getInstance().getAllByAmenityType(amenity.get().getAmenityTypeId());

            Review review = new Review();
            review.setUserId(user.getId());

            review.setAmenityId(amenityId);

            String name = request.getParameter("name");
            String description = request.getParameter("description");

            if (name == null || name.isEmpty() || description == null || description.isEmpty()) {
                request.setAttribute("alert", new Alert("danger", "Please fill out all fields."));
                request.getRequestDispatcher("/template/reviews/form.jsp").forward(request, response);
                return;
            }

            review.setName(name);
            review.setDescription(description);

            ReviewDao.create(review);

            for (AmenityTypeMetric metric : metrics) {
                AmenityTypeMetricRecord metricRecord = new AmenityTypeMetricRecord();
                metricRecord.setReviewId(review.getId());
                metricRecord.setAmenityMetricId(metric.getId());
                int value = Util.parseIntOrDefault(request.getParameter("metric-" + metric.getId()), 0);

                if (value < 0) {
                    value = 0;
                } else if (value > 5) {
                    value = 5;
                }

                metricRecord.setValue(value);

                ReviewDao.createReviewRecord(metricRecord);
            }

            for (Part part : request.getParts()) {
                if (part.getName().equals("images")) {
                    try {
                        String imageUrl = S3.uploadFile(
                                "little-hidden-gems",
                                "review-image-" + review.getId() + "-" + UUID.randomUUID(),
                                part
                        );
                        ReviewDao.createReviewImage(review.getId(), imageUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            response.sendRedirect(request.getContextPath() + "/reviews?f=list&id=" + amenityId + "#review-" + review.getId());

            return;
        }

        List<AmenityTypeMetric> metrics = AmenityTypeMetricDao.getInstance().getAllByAmenityType(amenity.get().getAmenityTypeId());
        List<AmenityTypeMetricRecordWithName> metricsWithNames = new ArrayList<>();

        // because we generalized the template to work with
        for (AmenityTypeMetric metric : metrics) {
            AmenityTypeMetricRecordWithName metricWithName = new AmenityTypeMetricRecordWithName();
            metricWithName.setAmenityMetricId(metric.getId());
            metricWithName.setName(metric.getName());
            metricsWithNames.add(metricWithName);
        }

        request.setAttribute("metrics", metricsWithNames);

        request.getRequestDispatcher("/template/reviews/form.jsp").forward(request, response);
    }

    public void getAll(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {

    }
}