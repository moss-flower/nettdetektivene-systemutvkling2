FROM oven/bun:1

WORKDIR /app

# Installer dependencies først for bedre cache
COPY package.json bun.lock ./
RUN bun install

# Kopier resten av prosjektet
COPY . .

EXPOSE 3000

CMD ["bun", "run", "dev", "--", "--host", "0.0.0.0", "--port", "3000"]