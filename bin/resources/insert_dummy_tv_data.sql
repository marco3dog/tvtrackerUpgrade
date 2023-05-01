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

insert into user_shows values (1, 1, 12);
insert into user_shows values (2, 7, 42);
insert into user_shows values (3, 9, 17);
insert into user_shows values (4, 2, 44);
insert into user_shows values (5, 9, 60);
insert into user_shows values (1, 5, 8);

select * from user_shows;

SELECT s.name, us.episodes, s.episodes FROM user_shows us JOIN user u ON us.userid = u.userid JOIN shows s ON us.showid = s.showid WHERE us.userid = 1;
SELECT s.showid, s.name, us.episodes, s.episodes FROM user_shows us JOIN user u ON us.userid = u.userid JOIN shows s ON us.showid = s.showid WHERE us.userid = 1;	
select userid from user where username = 'sheen';