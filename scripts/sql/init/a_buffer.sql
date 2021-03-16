SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: dvdrental; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE movement_data WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'de_DE.UTF-8' LC_CTYPE = 'de_DE.UTF-8';


ALTER DATABASE movement_data OWNER TO postgres;

\connect movement_data

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: _group_concat(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION _group_concat(text, text) RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $_$
SELECT CASE
  WHEN $2 IS NULL THEN $1
  WHEN $1 IS NULL THEN $2
  ELSE $1 || ', ' || $2
END
$_$;


ALTER FUNCTION public._group_concat(text, text) OWNER TO postgres;


CREATE SEQUENCE rawmovement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE rawmovement_id_seq OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;



CREATE TABLE rawmovement (
    id BIGINT DEFAULT nextval('rawmovement_id_seq'::regclass) NOT NULL,
    user_id bigint NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    time_stamp timestamp without time zone DEFAULT now()
  
);


ALTER TABLE rawmovement OWNER TO postgres;

--GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO admin;
--GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public to admin;
