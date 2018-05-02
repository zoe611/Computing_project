CREATE TABLE articlesv2
(
    article_id BIGINT NOT NULL,
    article_id_pk INTEGER DEFAULT nextval('articles_article_id_pk_seq'::regclass) PRIMARY KEY NOT NULL,
    article_title TEXT,
    searchterm TEXT,
    article_summary JSON,
    first_author TEXT,
    publisher_name TEXT,
    source TEXT,
    sort_title TEXT,
    pub_type TEXT,
    elocation_id TEXT,
    authtype TEXT,
    author_name TEXT,
    clusterid TEXT,
    pub_date TEXT
);
CREATE TABLE articlesv3
(
    article_id BIGINT NOT NULL,
    article_id_pk INTEGER DEFAULT nextval('articles_article_id_pk_seq'::regclass) PRIMARY KEY NOT NULL,
    article_title TEXT,
    searchterm TEXT,
    article_summary JSON,
    first_author TEXT,
    publisher_name TEXT,
    source TEXT,
    sort_title TEXT,
    pub_type TEXT,
    elocation_id TEXT,
    authtype TEXT,
    author_name TEXT,
    clusterid TEXT,
    pub_date TEXT,
    article_uid BIGINT
);
CREATE TABLE authors
(
    author_id INTEGER DEFAULT nextval('authors_author_id_seq'::regclass) PRIMARY KEY NOT NULL,
    author_name TEXT,
    authtype TEXT,
    clusterid TEXT,
    ref_articles_article_id INTEGER,
    fk_article_id_pk INTEGER
);
CREATE TABLE authors_articles
(
    author_id BIGINT,
    article_id BIGINT
);
CREATE TABLE authorsv2
(
    author_id INTEGER DEFAULT nextval('authors_author_id_seq'::regclass) PRIMARY KEY NOT NULL,
    author_name TEXT,
    authtype TEXT,
    clusterid TEXT,
    ref_articles_article_id INTEGER,
    fk_article_id_pk INTEGER,
    CONSTRAINT fk_authorsv2_articlesv2 FOREIGN KEY (fk_article_id_pk) REFERENCES articlesv2 (article_id_pk)
);
CREATE TABLE authorsv3
(
    author_id INTEGER DEFAULT nextval('authors_author_id_seq'::regclass) PRIMARY KEY NOT NULL,
    author_name TEXT,
    authtype TEXT,
    clusterid TEXT,
    ref_articles_article_id INTEGER,
    fk_article_id_pk INTEGER,
    CONSTRAINT fk_authorsv3_articlesv3 FOREIGN KEY (fk_article_id_pk) REFERENCES articlesv3 (article_id_pk)
);
CREATE TABLE databasechangelog
(
    id VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    dateexecuted TIMESTAMP NOT NULL,
    orderexecuted INTEGER NOT NULL,
    exectype VARCHAR(10) NOT NULL,
    md5sum VARCHAR(35),
    description VARCHAR(255),
    comments VARCHAR(255),
    tag VARCHAR(255),
    liquibase VARCHAR(20),
    contexts VARCHAR(255),
    labels VARCHAR(255),
    deployment_id VARCHAR(10)
);
CREATE TABLE databasechangeloglock
(
    id INTEGER PRIMARY KEY NOT NULL,
    locked BOOLEAN NOT NULL,
    lockgranted TIMESTAMP,
    lockedby VARCHAR(255)
);
CREATE TABLE hits_idlist
(
    data JSON,
    id INTEGER DEFAULT nextval('terms_idlist_id_seq'::regclass) PRIMARY KEY NOT NULL,
    timestamp_ TIMESTAMP WITH TIME ZONE DEFAULT clock_timestamp() NOT NULL
);
CREATE TABLE terms_idlist
(
    term TEXT,
    idlist JSON,
    id INTEGER DEFAULT nextval('terms_idlist_id_seq'::regclass) PRIMARY KEY NOT NULL,
    hits INTEGER,
    article_summary_list JSON
);
CREATE UNIQUE INDEX "terms_idList_term_uindex" ON terms_idlist (term);
CREATE UNIQUE INDEX "terms_idList_id_uindex" ON terms_idlist (id);
CREATE TABLE terms_idlistv2
(
    term TEXT,
    idlist JSON,
    id INTEGER DEFAULT nextval('terms_idlist_id_seq'::regclass) PRIMARY KEY NOT NULL,
    hits INTEGER,
    article_summary_list JSON
);
CREATE UNIQUE INDEX "terms_idList_term_uindex2" ON terms_idlistv2 (term);
CREATE UNIQUE INDEX "terms_idList_id_uindex2" ON terms_idlistv2 (id);
CREATE TABLE terms_idlistv3
(
    term TEXT,
    idlist JSON,
    id INTEGER DEFAULT nextval('terms_idlist_id_seq'::regclass) PRIMARY KEY NOT NULL,
    hits INTEGER,
    article_summary_list JSON
);
CREATE TABLE unique_articles
(
    article_id BIGINT NOT NULL,
    article_id_pk INTEGER,
    article_title TEXT,
    searchterm TEXT,
    article_summary JSON,
    authors TEXT
);
CREATE UNIQUE INDEX articles_article_id_uindex ON unique_articles (article_id);
CREATE TABLE unique_authors
(
    id INTEGER DEFAULT nextval('unique_authors_id_seq'::regclass) PRIMARY KEY NOT NULL,
    author_id INTEGER DEFAULT nextval('unique_authors_author_id_seq'::regclass) NOT NULL,
    author_name TEXT
);
CREATE UNIQUE INDEX unique_authors_author_id_uindex ON unique_authors (author_id);
CREATE TABLE user_account
(
    user_id INTEGER DEFAULT nextval('account_account_id_seq'::regclass) PRIMARY KEY NOT NULL,
    first_name TEXT,
    last_name TEXT,
    email TEXT,
    password TEXT,
    orcid TEXT,
    title TEXT,
    affiliation TEXT,
    organisation TEXT,
    key_areas_of_research_interest TEXT,
    most_relevant_publications TEXT,
    current_projects TEXT,
    looking_for TEXT[],
    confirmation_key TEXT,
    enabled BOOLEAN,
    created_at TIMESTAMP,
    created_by TEXT,
    modified_at TIMESTAMP,
    modified_by TEXT,
    short_bio TEXT,
    address TEXT,
    website TEXT,
    google_plus TEXT,
    linkedin TEXT,
    twitter TEXT,
    facebook TEXT,
    user_type TEXT
);
CREATE UNIQUE INDEX user_account_email_uindex ON user_account (email);
CREATE TABLE user_profile
(
    user_profile_pk INTEGER DEFAULT nextval('user_profile_user_profile_pk_seq'::regclass) PRIMARY KEY NOT NULL,
    user_account_id INTEGER,
    user_profile_first_name TEXT,
    user_profile_last_name TEXT,
    user_profile_display_name TEXT,
    user_profile_headline TEXT,
    user_profile_current_position TEXT,
    user_profile_country TEXT,
    user_profile_zipcode TEXT,
    user_profile_summary TEXT,
    user_profile_orcid TEXT,
    user_profile_key_areas_of_interest TEXT[],
    user_profile_most_relevant_publications TEXT[],
    user_profile_current_projects TEXT[],
    user_profile_looking_for TEXT[],
    user_profile_website TEXT,
    user_profile_google_plus TEXT,
    user_profile_linkedin TEXT,
    user_profile_twitter TEXT,
    user_profile_facebook TEXT,
    user_profile_experience_pk INTEGER,
    user_profile_education_pk INTEGER,
    user_profile_picture_pk INTEGER,
    created_at TIMESTAMP,
    modified_at TIMESTAMP
);
CREATE TABLE user_profile_picture
(
    user_profile_picture_pk INTEGER DEFAULT nextval('user_profile_picture_user_profile_picture_pk_seq'::regclass) PRIMARY KEY NOT NULL,
    user_profile_display_picture BYTEA,
    user_profile_pk INTEGER,
    CONSTRAINT user_profile_picture_user_profile_pk_fkey FOREIGN KEY (user_profile_pk) REFERENCES user_profile (user_profile_pk) ON DELETE CASCADE
);
CREATE FUNCTION extractuid() RETURNS VOID;
CREATE FUNCTION extractuid2() RETURNS VOID;
CREATE FUNCTION find_related_authors(name TEXT) RETURNS TABLE(AUTHOR TEXT, ARTICLEID INTEGER);
