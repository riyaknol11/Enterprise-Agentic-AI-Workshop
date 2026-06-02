# langchain4j-chatbot

## Project Overview

This repository contains an AI Chatbot built using **LangChain4j** and **Spring Boot** as part of the Day 1 Workshop assignment.

## What is ShopBot?

**ShopBot** is an AI-powered shopping assistant that can:
- Have multi-turn conversations (remembers what you said earlier)
- Look up product details from a catalog
- Calculate prices with discounts
- Tell you the current date and time

The chatbot uses **OpenAI GPT-3.5-Turbo** as its AI brain, connected via **LangChain4j** — a Java framework for building AI-powered applications.

## Repository Structure

```
langchain4j-chatbot-[your-name]/
├── README.md                    ← You are here
└── day1-chatbot-workshop/       ← The main Spring Boot application
    ├── pom.xml
    └── src/...
```

## Quick Start

See the full documentation in [day2-chatbot-workshop/README.md](./README.md).

```bash
cd day2-chatbot-workshop
export OPENAI_API_KEY=sk-your-key
mvn spring-boot:run
```
