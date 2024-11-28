CREATE MATERIALIZED VIEW student_lab_ratings AS
SELECT s.id         as student_id,
       s.username,
       s.first_name,
       s.second_name,
       COUNT(ls.id) AS completed_labs
FROM "user" s
         JOIN session ls ON s.id = ls.student_id
WHERE ls.status = 'SUCCESS'
GROUP BY s.id, s.username
ORDER BY completed_labs DESC;

CREATE OR REPLACE FUNCTION refresh_materialized_view()
    RETURNS TRIGGER AS
$$
BEGIN
    REFRESH MATERIALIZED VIEW student_lab_ratings;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER refresh_student_lab_ratings
    AFTER INSERT OR
        UPDATE
    ON session
    FOR EACH ROW
    WHEN (NEW.status = 'SUCCESS')
EXECUTE FUNCTION refresh_materialized_view();
