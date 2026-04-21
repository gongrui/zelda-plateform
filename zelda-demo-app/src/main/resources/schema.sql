-- 订单表
CREATE TABLE IF NOT EXISTS t_order (
    id BIGINT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    product_name VARCHAR(255) NOT NULL COMMENT '商品名称',
    amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    status INTEGER DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    remark TEXT COMMENT '备注'
);

COMMENT ON TABLE t_order IS '订单表';
COMMENT ON COLUMN t_order.id IS '订单 ID';
COMMENT ON COLUMN t_order.order_no IS '订单编号';
COMMENT ON COLUMN t_order.user_id IS '用户 ID';
COMMENT ON COLUMN t_order.product_name IS '商品名称';
COMMENT ON COLUMN t_order.amount IS '订单金额';
COMMENT ON COLUMN t_order.status IS '订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消';
COMMENT ON COLUMN t_order.create_time IS '创建时间';
COMMENT ON COLUMN t_order.update_time IS '更新时间';
COMMENT ON COLUMN t_order.remark IS '备注';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_order_user_id ON t_order(user_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON t_order(status);
CREATE INDEX IF NOT EXISTS idx_order_create_time ON t_order(create_time);
