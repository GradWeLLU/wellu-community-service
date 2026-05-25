CREATE TABLE post_reactions (
    id UUID PRIMARY KEY,
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id UUID NOT NULL,
    type VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_post_reactions_post_user UNIQUE (post_id, user_id)
);

CREATE INDEX idx_post_reactions_post_id
    ON post_reactions (post_id);

CREATE INDEX idx_post_reactions_user_id
    ON post_reactions (user_id);
