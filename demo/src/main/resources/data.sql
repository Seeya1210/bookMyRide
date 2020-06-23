-------CREATE TABLES---------------------------------------------

CREATE TABLE user (
  id BIGINT NOT NULL,
  first_name VARCHAR(64) NOT NULL,
  last_name VARCHAR(64) NOT NULL,
  phone_no VARCHAR(24) NOT NULL,
  email VARCHAR(64) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `location` (
  `id` BIGINT NOT NULL,
  `name` VARCHAR(256) NOT NULL,
  `latitude` DECIMAL(10,8) NOT NULL,
  `longitude` DECIMAL(11,8) NOT NULL,
  PRIMARY KEY (`id`));

 CREATE TABLE IF NOT EXISTS `cab_info` (
  `id` BIGINT NOT NULL,
  `type` VARCHAR(64) NOT NULL,
  `number` VARCHAR(36) NOT NULL,
  `description` VARCHAR(36) NULL,
  `driver_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `driver_id_fk`
    FOREIGN KEY (`driver_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS `trip_info` (
  `id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `cab_id` BIGINT NOT NULL,
  `create_ts` TIMESTAMP(6) NOT NULL,
  `update_ts` TIMESTAMP(6) NULL,
  `destination_id` BIGINT(64) NOT NULL,
  `source_id` BIGINT(64) NOT NULL,
  `fare` INT NULL,
  `distance_km` DECIMAL(10,0)  NULL,
  `payment_mode` VARCHAR(64) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `user_id_fk`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `cab_id_fk`
    FOREIGN KEY (`cab_id`)
    REFERENCES `cab_info` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `dist_id_fk`
    FOREIGN KEY (`destination_id`)
    REFERENCES `location` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `source_id_fk`
    FOREIGN KEY (`source_id`)
    REFERENCES `location` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
    
    
 -------------------------------INSERTS FOR USERS AND CABS---------------------------
 
INSERT INTO `user` (`id`, `first_name`, `last_name`, `phone_no`, `email`) VALUES (1, 'Alex', 'Dunphy', '+91 8739027654', 'alex.dunphy@gmail.com');
INSERT INTO `user` (`id`, `first_name`, `last_name`, `phone_no`, `email`) VALUES (2, 'Phil', 'Dunphy', '+91 8739027633', 'phil.dunphy@gmail.com');
INSERT INTO `user` (`id`, `first_name`, `last_name`, `phone_no`, `email`) VALUES (3, 'Claire', 'Dunphy', '+91 8739023433', 'claire.dunphy@gmail.com');
INSERT INTO `user` (`id`, `first_name`, `last_name`, `phone_no`, `email`) VALUES (4, 'Luke', 'Dunphy', '+91 8735523433', 'luke.dunphy@gmail.com');
INSERT INTO `user` (`id`, `first_name`, `last_name`, `phone_no`, `email`) VALUES (5, 'Haley', 'Dunphy', '+91 8735523243', 'haley.dunphy@gmail.com');
INSERT INTO `user` (`id`, `first_name`, `last_name`, `phone_no`, `email`) VALUES (6, 'Mitchell', 'Pritchett', '+91 8739024514', 'mitchell.Pritchett@gmail.com');
INSERT INTO `user` (`id`, `first_name`, `last_name`, `phone_no`, `email`) VALUES (7, 'Cameron', 'Tucker', '+91 8737657633', 'cameron.tucker@gmail.com'); 
INSERT INTO `user` (`id`, `first_name`, `last_name`, `phone_no`, `email`) VALUES (8, 'Jay', 'Pritchett', '+91 8739023453', 'jay.pritchett@gmail.com');
INSERT INTO `user` (`id`, `first_name`, `last_name`, `phone_no`, `email`) VALUES (9, 'Dylan', 'Williams', '+91 8733623433', 'dylan.williams@gmail.com');
INSERT INTO `user` (`id`, `first_name`, `last_name`, `phone_no`, `email`) VALUES (10, 'Manny', 'Delgado', '+91 7735523433', 'manny.delgado@gmail.com');


INSERT INTO `cab_info` (`id`, `type`, `number`, `description`, `driver_id`) VALUES (1, 'SEDAN', 'MH 15 AC 2982', 'Silver Swift Dzire', 10);
INSERT INTO `cab_info` (`id`, `type`, `number`, `description`, `driver_id`) VALUES (2, 'MINI', 'MH 15 BD 2978', 'White Swift', 9);
INSERT INTO `cab_info` (`id`, `type`, `number`, `description`, `driver_id`) VALUES (3, 'AUTO', 'MH 15 DC 2987', 'Auto', 8);
INSERT INTO `cab_info` (`id`, `type`, `number`, `description`, `driver_id`) VALUES (4, 'MINI', 'MH 15 AB 8982', 'Brown Ritz', 7);
INSERT INTO `cab_info` (`id`, `type`, `number`, `description`, `driver_id`) VALUES (5, 'MICRO', 'MH 15 SN 2098', 'White Wagnor', 6);


INSERT INTO `location` (`id`, `name`, `latitude`, `longitude`) VALUES (1, 'Gangapur Road, Nashik 13', 19.95339400, 73.83771900); 
INSERT INTO `location` (`id`, `name`, `latitude`, `longitude`) VALUES (2, 'Dwarka, Nashik 1', 19.95339400, 73.83771900); 
INSERT INTO `location` (`id`, `name`, `latitude`, `longitude`) VALUES (3, 'Nashik Road, Nashik 6', 19.95339400, 73.83771900); 
INSERT INTO `location` (`id`, `name`, `latitude`, `longitude`) VALUES (4, 'City Centre, Nashik 9', 19.95339400, 73.83771900); 
INSERT INTO `location` (`id`, `name`, `latitude`, `longitude`) VALUES (5, 'Anandwali,  Nashik 13', 19.95339400, 73.83771900); 
INSERT INTO `location` (`id`, `name`, `latitude`, `longitude`) VALUES (6, 'College Road, Nashik 13', 19.95339400, 73.83771900); 
INSERT INTO `location` (`id`, `name`, `latitude`, `longitude`) VALUES (7, 'Canada Corner, Nashik 13', 19.95339400, 73.83771900); 


INSERT INTO `trip_info` (`id`, `user_id`, `cab_id`, `create_ts`,  `destination_id`, `source_id`) VALUES (1, 2, 1, '2020-06-21 02:54:32.944000', 1, 2); 
INSERT INTO `trip_info` (`id`, `user_id`, `cab_id`, `create_ts`,  `destination_id`, `source_id`) VALUES (2, 2, 4, '2020-06-22 05:54:32.944000', 2, 1); 
INSERT INTO `trip_info` (`id`, `user_id`, `cab_id`, `create_ts`,  `destination_id`, `source_id`) VALUES (3, 4, 1, '2020-06-22 23:54:32.944000', 3, 2); 
INSERT INTO `trip_info` (`id`, `user_id`, `cab_id`, `create_ts`,  `destination_id`, `source_id`) VALUES (4, 4, 3, '2020-06-21 11:54:32.944000', 4, 1); 
INSERT INTO `trip_info` (`id`, `user_id`, `cab_id`, `create_ts`,  `destination_id`, `source_id`) VALUES (5, 4, 2, '2020-06-21 12:54:32.944000', 1, 2); 