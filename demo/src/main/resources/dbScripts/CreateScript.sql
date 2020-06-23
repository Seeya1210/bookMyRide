
CREATE DATABASE `rides` ;
CREATE TABLE IF NOT EXISTS `rides`.`user` (
  `id` BIGINT(64) NOT NULL,
  `first_name` VARCHAR(64) NOT NULL,
  `last_name` VARCHAR(64) NOT NULL,
  `phone_no` VARCHAR(24) NOT NULL,
  `email` VARCHAR(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `phone_no_UNIQUE` (`phone_no` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `rides`.`location` (
  `id` BIGINT(64) NOT NULL,
  `name` VARCHAR(256) NULL,
  `latitude` DECIMAL(10,8) NOT NULL,
  `longitude` DECIMAL(11,8) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `rides`.`cab_info` (
  `id` BIGINT(64) NOT NULL,
  `type` VARCHAR(64) NOT NULL,
  `number` VARCHAR(36) NOT NULL,
  `description` VARCHAR(64) NULL,
  `driver_id` BIGINT(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `number_UNIQUE` (`number` ASC) VISIBLE,
  INDEX `driver_id_fk_idx` (`driver_id` ASC) VISIBLE,
  CONSTRAINT `driver_id_fk`
    FOREIGN KEY (`driver_id`)
    REFERENCES `rides`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `rides`.`trip_info` (
  `id` BIGINT(64) NOT NULL,
  `user_id` BIGINT(64) NOT NULL,
  `cab_id` BIGINT(64) NOT NULL,
  `create_ts` TIMESTAMP(6) NOT NULL,
  `update_ts` TIMESTAMP(6) NULL,
  `destination_id` BIGINT(64) NOT NULL,
  `source_id` BIGINT(64) NOT NULL,
  `fare` INT  NULL,
  `distance_km` DECIMAL(10,0) NULL,
  `payment_mode` VARCHAR(64) NULL,
  PRIMARY KEY (`id`),
  INDEX `user_id_fk_idx` (`user_id` ASC) VISIBLE,
  INDEX `dist_id_fk_idx` (`destination_id` ASC) VISIBLE,
  INDEX `source_id_fk_idx` (`source_id` ASC) VISIBLE,
  INDEX `cab_id_fk_idx` (`cab_id` ASC) VISIBLE,
  CONSTRAINT `user_id_fk`
    FOREIGN KEY (`user_id`)
    REFERENCES `rides`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `cab_id_fk`
    FOREIGN KEY (`cab_id`)
    REFERENCES `rides`.`cab_info` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `dist_id_fk`
    FOREIGN KEY (`destination_id`)
    REFERENCES `rides`.`location` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `source_id_fk`
    FOREIGN KEY (`source_id`)
    REFERENCES `rides`.`location` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-----STORED PROCEDURE FOR INSERTING TRIP DETAILS-----------------

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `bookRide`(IN t_user_id BIGINT, IN t_cab_id BIGINT, IN t_s_name VARCHAR(256), IN t_s_lati DECIMAL(10,8), IN t_s_longi DECIMAL(11,8),IN t_d_name VARCHAR(256), IN t_d_lati DECIMAL(10,8), IN t_d_longi DECIMAL(11,8), IN t_current_ts TIMESTAMP(6), OUT t_trip_Id BIGINT)
BEGIN
INSERT INTO `rides`.`location` ( `name`, `latitude`, `longitude`) VALUES ( t_s_name, t_s_lati, t_s_longi);
SELECT LAST_INSERT_ID() INTO @mysql_source_id;
INSERT INTO `rides`.`location` ( `name`, `latitude`, `longitude`) VALUES ( t_d_name, t_d_lati, t_d_longi);
SELECT LAST_INSERT_ID() INTO @mysql_desti_id;
INSERT INTO `rides`.`trip_info` (`user_id`,`cab_id`,`create_ts`,`destination_id`,`source_id`)VALUES(t_user_id,t_cab_id,t_current_ts,@mysql_desti_id,@mysql_source_id);
SELECT LAST_INSERT_ID() INTO @mysql_trip_id;
set t_trip_Id = @mysql_trip_id;
END$$
DELIMITER ;