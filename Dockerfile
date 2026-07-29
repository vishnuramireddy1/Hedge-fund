# Dockerfile for Hedge‑Fund app
# -------------------------------------------------
# Build stage – install only production dependencies
FROM node:22-alpine AS builder

# Create app directory
WORKDIR /app

# Copy package manifests
COPY package.json package-lock.json* ./

# Install production dependencies (skip dev)
RUN npm install --production

# Copy source code (everything else)
COPY . .

# -------------------------------------------------
# Runtime stage – lean image
FROM node:22-alpine

WORKDIR /app

# Copy node_modules from builder
COPY --from=builder /app/node_modules ./node_modules
# Copy application source
COPY --from=builder /app .

# Expose the port the app runs on (default 3000)
EXPOSE 3000

# Start the server
CMD ["npm", "start"]
