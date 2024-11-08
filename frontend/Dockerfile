FROM node:18.17.0-alpine AS builder

RUN apk update && apk add --no-cache libc6-compat

# Create app directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies safely
RUN npm i

# Copy the rest of the code
COPY . .

ARG NEXT_PUBLIC_API_URL
ARG NEXT_PUBLIC_SPRINGBOOT_API_URL
ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL
ENV NEXT_PUBLIC_SPRINGBOOT_API_URL=$NEXT_PUBLIC_SPRINGBOOT_API_URL

# Print the contents of node_modules to verify installation, then build the app
RUN ls -la node_modules | grep framer-motion && npm run build

# Production image, copy all the files and run next
FROM node:18.17.0-alpine AS runner
WORKDIR /app

# Set environment variables
ENV NODE_ENV=production

# Copy built assets from the builder stage
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json

COPY --from=builder /app/public ./public

# Create a non-root user
RUN addgroup -g 1001 -S nodejs
RUN adduser -S nextjs -u 1001

# Change ownership of the app directory to the non-root user
RUN chown -R nextjs:nodejs /app
# Switch to non-root user
USER nextjs

# Expose the port the app runs on
EXPOSE 3000

# Start the app
CMD ["npm", "start"]
