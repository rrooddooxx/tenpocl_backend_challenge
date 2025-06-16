-- definiciones sql para "backend tenpo challenge" realizado x sebastián kravetz para tenpo chile

-- 1. tabla "request_history_type" alberga el tipo de respuesta a las solicitudes, para el registro
-- de solicitudes
CREATE TABLE request_history_type (
                                      type_id int2 generated always as identity unique,
                                      type text unique
);

INSERT INTO request_history_type(type) VALUES ('SUCCESS');
INSERT INTO request_history_type(type) VALUES ('ERROR');

-- SELECT * FROM request_history_type;


--
--


-- 2. tabla "request_history" alberga el el registro de solicitudes
CREATE TABLE request_history (
                                 request_id bigint generated always as identity primary key,
                                 requested_at timestamptz default current_timestamp,
                                 endpoint text not null,
                                 response_type int2 references request_history_type(type_id) not null ,
                                 call_parameters jsonb not null,
                                 response jsonb not null
);

-- índices para optimizar consultas
CREATE INDEX idx_request_history_date ON request_history(requested_at);
CREATE INDEX idx_request_history_type ON request_history(response_type);
CREATE INDEX idx_request_history_rid ON request_history(endpoint);

-- SELECT * FROM request_history;