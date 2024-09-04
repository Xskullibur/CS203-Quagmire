// cypress/e2e/auth.cy.js

describe('Authentication', () => {
    beforeEach(() => {
        cy.request({
            method: 'POST',
            url: 'http://localhost:8080/test/remove-test-user',
            failOnStatusCode: false
        }).then((response) => {
            if (response.status !== 200) {
                cy.log(`Failed to remove test user. Status: ${response.status}, Body: ${response.body}`);
            } else {
                cy.log('Test user removed successfully');
            }
        });
    });

    it('should allow user to register and login', () => {
        // Visit the register page
        cy.visit('/auth/register');

        // Fill out the registration form
        cy.get('input[name="username"]').type('testuser');
        cy.get('input[name="email"]').type('testuser@example.com');
        cy.get('input[name="password"]').type('password123');
        cy.get('input[name="confirmPassword"]').type('password123');

        // Intercept the registration API call
        cy.intercept('POST', '**/authentication/register').as('registerRequest');

        // Submit the form
        cy.get('form').submit();

        // Wait for the registration request and check its response
        cy.wait('@registerRequest').then((interception) => {
            expect(interception.response.statusCode).to.equal(201);
        });

        // Assert successful registration (adjust based on your UI)
        cy.url().should('include', '/auth/login');

        // Now login
        cy.get('input[name="username"]').type('testuser');
        cy.get('input[name="password"]').type('password123');

        // Intercept the login API call
        cy.intercept('POST', '**/authentication/login').as('loginRequest');

        // Submit the login form
        cy.get('form').submit();

        // Wait for the login request and check its response
        cy.wait('@loginRequest').its('response.statusCode').should('eq', 200);

        // Assert successful login (adjust based on your UI)
        cy.url().should('include', '/profile');
        cy.contains('Welcome, testuser');
    });

    afterEach(() => {
        cy.request({
            method: 'POST',
            url: 'http://localhost:8080/test/reset-db',
            failOnStatusCode: false
        }).then((response) => {
            if (response.status !== 200) {
                cy.log(`Failed to reset DB. Status: ${response.status}, Body: ${response.body}`);
            }
        });
    });
});