CREATE TABLE IF NOT EXISTS topics (
    id bigint not null auto_increment,
    topic_enum enum ('BROKER_RECOMMENDATIONS','EDUCATION_AND_LEARNING','FOREX_AND_TAXES','FOREX_BASICS','FUNDAMENTAL_ANALYSIS','NEWS_AND_EVENTS','OTHER','RETIREMENT_PLANNING','RISK_MANAGEMENT','SCAMS_AND_FRAUDS','TECHNICAL_ANALYSIS','TIPS_AND_TRICKS','TRADING_AND_PSYCHOLOGY','TRADING_AUTOMATION','TRADING_BOOKS','TRADING_FOR_A_LIVING','TRADING_JOURNALS','TRADING_SOFTWARE','TRADING_STRATEGIES','TRADING_TOOLS') not null,
    primary key (id)
)