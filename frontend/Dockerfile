FROM node:18.17.0-alpine AS builder

RUN apk add --no-cache libc6-compat
RUN apk update

# Create app directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies safely
RUN npm ci --ignore-scripts

# If framer-motion is still missing, install it explicitly
RUN if ! [ -d "node_modules/framer-motion" ]; then npm install framer-motion --ignore-scripts; fi

# Copy the rest of the code
COPY . .

# Print the contents of node_modules to verify installation
RUN ls -la node_modules | grep framer-motion

# Build the app
RUN npm run build

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

# Expose the port the app runs on
EXPOSE 3000

# Start the app
CMD ["npm", "start"]