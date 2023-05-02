use tvtracker;

insert into shows values(null, 'Breaking Bad', 62);
insert into shows values(null, 'Game of Thrones', 73);
insert into shows values(null, 'The Office', 188);
insert into shows values(null, 'Mad Men', 92);
insert into shows values(null, 'Sons of Anarchy', 92);
insert into shows values(null, 'Dexter', 96);
insert into shows values(null, 'The Sopranos', 86);
insert into shows values(null, 'The Wire', 60);
insert into shows values(null, 'Jimmy Neutron', 61);
insert into shows values(null, 'True Detective', 25);

select * from shows;

insert into user values(null, 'walter', 'chemistry', "ADMIN" );
insert into user values(null, 'tony', 'gabagool', "USER" );
insert into user values(null, 'jimmy', 'brainblast', "USER");
insert into user values(null, 'hodor', 'hodor', "USER");
insert into user values(null, 'sheen', 'ultralord', "ADMIN");

select * from user;

insert into user_shows values (1, 1, 12, 3);
<<<<<<< HEAD
insert into user_shows values (2, 7, 42, 4);
insert into user_shows values (3, 9, 17, 5);
insert into user_shows values (4, 2, 44, 5);
insert into user_shows values (5, 9, 60, 1);
insert into user_shows values (1, 5, 8, 2);
=======
insert into user_shows values (2, 7, 42, 1);
insert into user_shows values (3, 9, 17, 5);
insert into user_shows values (4, 2, 44, 4);
insert into user_shows values (5, 9, 60, 2);
insert into user_shows values (1, 5, 8, 4);
>>>>>>> a48b7711fce15f8d6b207f31c4ef73b6eef70340

select * from user_shows;

SELECT s.name, us.episodes, s.episodes FROM user_shows us JOIN user u ON us.userid = u.userid JOIN shows s ON us.showid = s.showid WHERE us.userid = 1;
SELECT s.showid, s.name, us.episodes, s.episodes FROM user_shows us JOIN user u ON us.userid = u.userid JOIN shows s ON us.showid = s.showid WHERE us.userid = 1;	
select userid from user where username = 'sheen';

select * from shows;
-- This query selects the shows that the user has not currently added 
select s.showid, s.name, s.episodes from shows s LEFT JOIN user_shows us on us.showid = s.showid and us.userid = 1 where us.userid is null;
-- Displays the shows that the user is currently watching 
select s.showid, s.name, s.episodes from shows s LEFT JOIN user_shows us on us.showid = s.showid where us.userid = 1;