# SDE II Task for Monk Commerce 2024
Coupons Management API for an E-commerce Website

## API Endpoints (Functional Requirements) :
### All cases mentioned in the problem statement are covered by the following API endpoints:
*  POST /coupons: Create a new coupon.
* GET /coupons: Retrieve all coupons.
* GET /coupons/{id}: Retrieve a specific coupon by its ID.
* PUT /coupons/{id}: Update a specific coupon by its ID.
* DELETE /coupons/{id}: Delete a specific coupon by its ID.
* POST /applicable-coupons: Fetch all applicable coupons for a given cart and
calculate the total discount that will be applied by each coupon.
* POST /apply-coupon/{id}: Apply a specific coupon to the cart and return the
updated cart with discounted prices for each item.

## Non-Functional Requirements:
* Added Swagger UI for API documentation.
* Dockerized the application.
* Makefile for building and running the application.
* Implemented unit tests for all endpoints.

## Design Patterns applied:
* Strategy to create, delete and modify for different coupon types. (less coupling and easily extensive)
* Chain of Responsibility for finding applicable coupons. (easily extensive)

## Added Postman testcase collection file

### Assumptions
For BuyXGeyYCoupon, only 1 product can be discounted.
