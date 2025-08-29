# Spring Boot Webhook SQL – 22BCE0830 (Even)

This app:
1) Registers a webhook on startup
2) Chooses the SQL question based on regNo (even → Question 2)
3) Sends the final SQL query to the returned webhook using the JWT token (`Authorization` header)


## Final SQL (Question 2)
```sql
SELECT e1.EMP_ID,
       e1.FIRST_NAME,
       e1.LAST_NAME,
       d.DEPARTMENT_NAME,
       COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT
FROM EMPLOYEE e1
JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID
LEFT JOIN EMPLOYEE e2
       ON e1.DEPARTMENT = e2.DEPARTMENT
      AND e2.DOB > e1.DOB
GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME
ORDER BY e1.EMP_ID DESC;
```

> Note: The assignment text shows two variants for the submit URL. This app prioritizes the **webhook URL from the generate response**, and falls back to `/hiring/testWebhook/JAVA` if needed.
