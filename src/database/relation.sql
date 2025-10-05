select o.studentId , u.studentId
from auth.studentauth o
join users.student u on u.studentId = o.studentId