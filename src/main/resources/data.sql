CREATE TABLE oauth_client_details (
  client_id               VARCHAR(256) PRIMARY KEY,
  resource_ids            VARCHAR(256),
  client_secret           VARCHAR(256),
  scope                   VARCHAR(256),
  authorized_grant_types  VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities             VARCHAR(256),
  access_token_validity   INTEGER,
  refresh_token_validity  INTEGER,
  additional_information  VARCHAR(4096),
  autoapprove             VARCHAR(256)
);
INSERT INTO oauth_client_details (
  client_id, resource_ids, client_secret, scope, authorized_grant_types,
  web_server_redirect_uri, authorities, access_token_validity,
  refresh_token_validity, additional_information, autoapprove)
VALUES
  ('arunclientid', '', 'secretclient', 'read,write', 'client_credentials,implicit,authorization_code',
                  'http://localhost:8081/client/login', 'view,modify,delete', NULL,
                  NULL, '{}', 'true');

