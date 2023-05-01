use tvtracker;

select * from user_shows;

-- Your rating for a show --
select s.name, rating
from user_shows us
join shows s on us.showid = s.showid
where us.userid = 1;

-- Average rating for a shows --
select s.name, avg(rating) 
from user_shows us
join shows s on us.showid = s.showid
group by us.showid;